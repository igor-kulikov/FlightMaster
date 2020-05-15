package ua.ikulikov.flightmaster.skyscannerservice.services;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ua.ikulikov.flightmaster.skyscannerservice.SkyScannerServiceException;
import ua.ikulikov.flightmaster.skyscannerservice.config.AmqpPropertyConfig;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightPollStatusMQ;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollStatus;
import ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata.FlightData;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDto;
import ua.ikulikov.flightmaster.skyscannerservice.utils.Utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SkyScannerService implements ISkyScannerService {
    private final DatabaseService databaseService;
    private final RestTemplate rest;
    private final AmqpTemplate amqpEventsPublisher;
    private final AmqpPropertyConfig amqpProps;

    private Logger logger = Logger.getLogger(SkyScannerService.class);

    @Value("#{${skyScanner.httpRequest.headersMap}}")
    private Map<String, String> httpRequestHeaders;

    @Value("${skyScanner.createSession.url}")
    private String createSessionUrl;

    @Value("${skyScanner.createSession.locationKeyPrefix}")
    private String createSessionLocationKeyPrefix;

    @Value("${skyScanner.createSession.maxAttemptsCount}")
    private byte createSessionMaxAttemptsCount;

    @Value("${skyScanner.pollSession.url}")
    private String pollSessionUrl;

    @Value("${skyScanner.pollSession.maxAttemptsCount}")
    private byte pollSessionMaxAttemptsCount;

    @Value("${skyScanner.pollSession.firstPollDelayMs}")
    private Integer firstPollDelayMs;

    @Value("${skyScanner.attemptsTimeoutMs}")
    private int attemptsTimeout;

    /**
     * The Live Pricing Service Session must be created before any pricing data can be obtained.
     * The request contains details of the locations, dates, passengers, cabin class and user details.
     * These parameters define the session, and cannot be changed within the session (with the exception of passenger numbers).
     * <p>
     * Persist pricing data to database.
     *
     * @param poll       flight request's details
     */
    @Override
    @Async("taskExecutor")
    public void proceedFlightPollRequest(FlightRequestPollDto poll) {
        poll = databaseService.persistPoll(poll, FlightRequestPollStatus.IN_PROGRESS);

        try {
            proceedSkyScannerPolling(poll);
        }
        catch (SkyScannerServiceException e) {
            poll = databaseService.persistPoll(poll, e.getFlightRequestPollStatus());
            FlightPollStatusMQ pollStatusMQ = new FlightPollStatusMQ(poll.getId(), poll.getFlightRequestPollStatus(), poll.getPollStatusDateTime());
            amqpEventsPublisher.convertAndSend(amqpProps.getExchange(), amqpProps.getEventRoutingKey(), pollStatusMQ);
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        //todo - take status time from SkyScanner response
        FlightPollStatusMQ pollStatusMQ = new FlightPollStatusMQ(poll.getId(), FlightRequestPollStatus.SUCCESS, LocalDateTime.now());
        amqpEventsPublisher.convertAndSend(amqpProps.getExchange(), amqpProps.getEventRoutingKey(), pollStatusMQ);
    }

    @Transactional
    public void proceedSkyScannerPolling(FlightRequestPollDto poll) throws SkyScannerServiceException {
        final Thread currentThread = Thread.currentThread();
        final String origThreadName = currentThread.getName();

        currentThread.setName(String.format("LP_(%s_%s)->(%s_%s)",
                poll.getOutboundAirport(), poll.getOutboundDate(),
                poll.getInboundAirport(), poll.getInboundDate()));

        String sessionKey = createSession(poll);
        Utils.sleep(firstPollDelayMs, "sleeping before first poll");
        Pair<FlightData, LocalDateTime> flightDataPair = pollSession(sessionKey);

        FlightData flightData = flightDataPair.getLeft();
        try {
            poll.setItineraries(flightData.getItineraries());
            databaseService.persistDicts(flightData);
            databaseService.persistPoll(poll, FlightRequestPollStatus.SUCCESS, flightDataPair.getRight());
        }
        catch (DataIntegrityViolationException e) {
            logger.error("DataIntegrityViolationException = ", e);
        }
        currentThread.setName(origThreadName);
    }

    /**
     * Creates the Live Pricing Service Session in SkyScanner for further obtaining pricing data.
     * Number of attempts is limited to pollSessionMaxAttemptsCount.
     *
     * @param pollRequest flight request's details
     * @return sessionKey identifier of created session in SkyScanner service
     */
    private String createSession(FlightRequestPollDto pollRequest) throws SkyScannerServiceException {
        HttpResponse<JsonNode> response;

        int attemptCounter = 0;
        do {
            attemptCounter++;

                logger.info(String.format("starting to create session: attempt %d of %d", attemptCounter, createSessionMaxAttemptsCount));

                Function<LocalDate, String> dateToStrFunc = date -> (date == null) ? "" : date.toString();
                response = Unirest.post(createSessionUrl)
                        .header("X-RapidAPI-Host", httpRequestHeaders.get("X-RapidAPI-Host"))
                        .header("X-RapidAPI-Key", httpRequestHeaders.get("X-RapidAPI-Key"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .field("country", pollRequest.getCountry())
                        .field("currency", pollRequest.getCurrency())
                        .field("locale", pollRequest.getLocale())
                        .field("originPlace", pollRequest.getOutboundAirport() + "-sky")
                        .field("destinationPlace", pollRequest.getInboundAirport() + "-sky")
                        .field("outboundDate", dateToStrFunc.apply(pollRequest.getOutboundDate()))
                        .field("inboundDate", dateToStrFunc.apply(pollRequest.getInboundDate()))
//                    .field("cabinClass", "business")
                        .field("adults", String.valueOf(pollRequest.getAdults()))
                        .field("children", String.valueOf(pollRequest.getChildren()))
                        .field("infants", String.valueOf(pollRequest.getInfants()))
                        .field("groupPricing", String.valueOf(pollRequest.getGroupPricing()))
                        .asJson();

            if (response.isSuccess()) {
                String sessionKey = response.getHeaders().get("Location").get(0).replace(createSessionLocationKeyPrefix, "");
                logger.info("session is created. sessionKey = " + sessionKey);
                return sessionKey;
            } else if (response.getStatus() == 400) {
                throw new SkyScannerServiceException(
                        FlightRequestPollStatus.FAILED_BAD_REQUEST,
                        String.format("Bad client request (Response status = %d %s): %s",
                                response.getStatus(), response.getStatusText(), response.getBody()));
            } else if (attemptCounter < createSessionMaxAttemptsCount) {
                logger.info(String.format("session is not created: %d (%s)", response.getStatus(), response.getStatusText()));
                Utils.sleep(attemptsTimeout, "sleeping between attempts");
            }
        } while (attemptCounter < createSessionMaxAttemptsCount);

        throw new SkyScannerServiceException(
                FlightRequestPollStatus.FAILED_SKYSCANNER,
                String.format("Can't establish live price session in %d attempts.", attemptCounter));
    }

    /**
     * Polls Live Pricing Service Session created in SkyScanner until response contains status = UpdatesComplete.
     * Number of attempts is limited to pollSessionMaxAttemptsCount.
     *
     * @param sessionKey identifier of session in SkyScanner service for polling of
     * @return pair of [session creation DateTime, flight live prices obtained]
     */
    private Pair<FlightData, LocalDateTime> pollSession(String sessionKey) throws SkyScannerServiceException {
        String url = pollSessionUrl.replace("{sessionkey}", sessionKey);

        FlightData flightData;
        int attemptCounter = 0;
        do {
            attemptCounter++;
            logger.info(String.format("starting to poll session: attempt %d of %d", attemptCounter, pollSessionMaxAttemptsCount));
            try {
                flightData = rest.exchange(url, HttpMethod.GET, getHttpRequest(), FlightData.class).getBody();
            }
            catch (RestClientException e) {
                logger.error("RestClientException = " + e);
                throw new RuntimeException(e);
            }
            if (attemptCounter < pollSessionMaxAttemptsCount && !isUpdatesComplete(flightData))
                Utils.sleep(attemptsTimeout, "sleeping between attempts");
        }
        while (attemptCounter < pollSessionMaxAttemptsCount && !isUpdatesComplete(flightData));

        if (isUpdatesComplete(flightData)) {
            LocalDateTime pollDateTime = LocalDateTime.now();
            logger.info("session is sucessfully polled: "
                    + flightData.getItineraries().size() + " itineraries;"
                    + flightData.getLegs().size() + " legs");
            return new ImmutablePair<>(flightData, pollDateTime);
        } else
            throw new SkyScannerServiceException(
                    FlightRequestPollStatus.FAILED_SKYSCANNER,
                    String.format("Can't poll live price session in %d attempts", attemptCounter));
    }

    private boolean isUpdatesComplete(FlightData flightData) {
        return flightData != null &&
                flightData.getStatus() != null &&
                flightData.getStatus().equals("UpdatesComplete");
    }

    private HttpEntity getHttpRequest() {
        HttpHeaders requestHeaders = new HttpHeaders();
        httpRequestHeaders.forEach((key, value) -> requestHeaders.add(key, value));
        return new HttpEntity(requestHeaders);
    }
}