package ua.ikulikov.flightmaster.skyscannerservice.services;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollStatus;
import ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata.FlightData;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPoll;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IAgentRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.ICarrierRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IFlightRequestPollRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IItineraryRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.ILegRepository;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IPlaceRepository;
import ua.ikulikov.flightmaster.skyscannerservice.utils.Utils;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SkyScannerService implements ISkyScannerService {
    private final IFlightRequestPollRepository flightRequestPollRepository;
    private final IAgentRepository agentRepository;
    private final ICarrierRepository carrierRepository;
    private final IPlaceRepository placeRepository;
    private final ILegRepository legRepository;
    private final IItineraryRepository itineraryRepository;
    private final RestTemplate rest;

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
     * @param flightRequestPoll       flight request's details
     * @param requestReceivedDateTime date time of receiving request
     */
    @Override
    @Async("taskExecutor")
    public void proceedFlightPollRequest(FlightRequestPoll flightRequestPoll, LocalDateTime requestReceivedDateTime) {
        flightRequestPoll.setRequestReceivedDateTime(requestReceivedDateTime);
        flightRequestPoll.setFlightRequestPollStatus(FlightRequestPollStatus.IN_PROGRESS);
        flightRequestPoll.setPollStatusDateTime(LocalDateTime.now());
        flightRequestPoll = flightRequestPollRepository.saveAndFlush(flightRequestPoll);

        try {
            proceedSkyScannerPolling(flightRequestPoll);
        }
        catch (SkyScannerServiceException e) {
            flightRequestPoll.setFlightRequestPollStatus(e.getFlightRequestPollStatus());
            flightRequestPoll.setPollStatusDateTime(LocalDateTime.now());
            flightRequestPoll = flightRequestPollRepository.saveAndFlush(flightRequestPoll);
            logger.error(e.getMessage());
        }
    }

    @Transactional
    public void proceedSkyScannerPolling(FlightRequestPoll flightRequestPoll) throws SkyScannerServiceException {
        final Thread currentThread = Thread.currentThread();
        final String origThreadName = currentThread.getName();

        currentThread.setName(String.format("LP_(%s_%s)->(%s_%s)",
                flightRequestPoll.getOutboundAirport(), flightRequestPoll.getOutboundDate(),
                flightRequestPoll.getInboundAirport(), flightRequestPoll.getInboundDate()));

        String sessionKey = createSession(flightRequestPoll);
        Utils.sleep(firstPollDelayMs, "sleeping before first poll");
        Pair<FlightData, LocalDateTime> flightDataPair = pollSession(sessionKey);

        FlightData flightData = flightDataPair.getLeft();
        try {
            agentRepository.saveAll(flightData.getAgents());
            carrierRepository.saveAll(flightData.getCarriers());
            placeRepository.saveAll(flightData.getPlaces());
            legRepository.saveAll(flightData.getLegs());
            itineraryRepository.saveAll((flightData.getItineraries()));

            flightRequestPoll.setFlightRequestPollStatus(FlightRequestPollStatus.SUCCESS);
            flightRequestPoll.setPollStatusDateTime(flightDataPair.getRight());
            flightRequestPoll.setItineraries(flightData.getItineraries());
            flightRequestPollRepository.save(flightRequestPoll);
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
    private String createSession(FlightRequestPoll pollRequest) throws SkyScannerServiceException {
        HttpResponse<JsonNode> response;

        int attemptCounter = 0;
        do {
            attemptCounter++;

                logger.info(String.format("starting to create session: attempt %d of %d", attemptCounter, createSessionMaxAttemptsCount));

                response = Unirest.post(createSessionUrl)
                        .header("X-RapidAPI-Host", httpRequestHeaders.get("X-RapidAPI-Host"))
                        .header("X-RapidAPI-Key", httpRequestHeaders.get("X-RapidAPI-Key"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .field("country", pollRequest.getCountry())
                        .field("currency", pollRequest.getCurrency())
                        .field("locale", pollRequest.getLocale())
                        .field("originPlace", pollRequest.getOutboundAirport() + "-sky")
                        .field("destinationPlace", pollRequest.getInboundAirport() + "-sky")
                        .field("outboundDate", pollRequest.getOutboundDate().toString())
                        .field("inboundDate", pollRequest.getInboundDate().toString())  // todo - fix "inboundDate is optional
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