package ua.ikulikov.flightmaster.flightrequestservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequest;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPoll;
import ua.ikulikov.flightmaster.flightrequestservice.repositories.FlightRequestPollRepository;
import ua.ikulikov.flightmaster.flightrequestservice.repositories.FlightRequestRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightRequestService implements IFlightRequestService {
    private final FlightRequestRepository flightRequestRepository;
    private final FlightRequestPollRepository flightRequestPollRepository;
    private final RestTemplate restTemplate;

    @Override
    public void processFlightRequest(FlightRequest request) {
    }

    @Override
    public List<FlightRequest> getFlightRequests() {
        return flightRequestRepository.findAll();
    }

    @Override
    public List<FlightRequest> getEnabledFlightRequests() {
        return getFlightRequests()
                .stream()
                .filter(FlightRequest::getEnabledFlag)
                .collect(Collectors.toList());
    }

    @Override
    public FlightRequest addFlightRequest(FlightRequest flightRequest) {
        return flightRequestRepository.saveAndFlush(flightRequest);
    }

    @Override
    public void deleteFlightRequest(long id) {
        flightRequestRepository.deleteById(id);
    }

    @Override
    public Optional<FlightRequest> disableFlightRequest(long id) {
        return setEnabledFlag(id, false);
    }

    @Override
    public Optional<FlightRequest> enableFlightRequest(long id) {
        return setEnabledFlag(id, true);
    }

    @Override
    public Optional<FlightRequest> setEnabledFlag(long id, boolean enabled) {
        Optional<FlightRequest> flightRequestOptional = flightRequestRepository.findById(id);
        if (flightRequestOptional.isPresent()) {
            FlightRequest flightRequest = flightRequestOptional.get();
            flightRequest.setEnabledFlag(enabled);
            return Optional.of(flightRequestRepository.save(flightRequest));
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public void pollFlightRequest(Long flightRequestId) {
        //get flightRequest object by Id
        Optional<FlightRequest> flightRequestOptional = flightRequestRepository.findById(flightRequestId);
        if (!flightRequestOptional.isPresent()) {
            return;     // todo - handle response properly
        }

        //get airports located in origin-place and destination-place
        FlightRequest flightRequest = flightRequestOptional.get();
        Set<String> originAirports = getChildAirportsInPlace(flightRequest.getOriginPlace());
        Set<String> destinationAirports = getChildAirportsInPlace(flightRequest.getDestinationPlace());

        for(String originAirport : originAirports)
            for (String destinationAirport : destinationAirports)
                for (int shiftDays = 0; shiftDays <= flightRequest.getSerialPollPeriod(); shiftDays++) {
                    LocalDate shiftedOutboundDate = flightRequest.getOutboundDate().plusDays(shiftDays);
                    LocalDate shiftedInboundDate = flightRequest.getInboundDate().plusDays(shiftDays);

                    // todo - add logger
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
                    System.out.println(String.format("Polling SkyScannerService with following parameters:\n" +
                            "  - airportOrigin = %s, airportDestination = %s\n" +
                            "  - shiftedOutboundDate = %s, shiftedInboundDate = %s",
                            originAirport, destinationAirport,
                            shiftedOutboundDate.format(formatter), shiftedInboundDate.format(formatter)));

                    FlightRequestPoll flightRequestPoll = new FlightRequestPoll(flightRequest,
                            originAirport, destinationAirport, shiftedOutboundDate, shiftedInboundDate);

                    // poll SkyScanner service for flights between airports
                    LocalDateTime sendPollRequestDateTime = postPollRequestToSkyScanner(flightRequestPoll);

                    flightRequestPoll.setPollDateTime(sendPollRequestDateTime);
                }
    }

    private Set<String> getChildAirportsInPlace(String place) {
        //todo - extract geoCatalog end-point to property
        ResponseEntity<Set<String>> placeAirportsResponse = restTemplate.exchange(
                "http://geo-catalog-service/getChild/" + place, HttpMethod.GET, null,
                new ParameterizedTypeReference<Set<String>>() {
                });
        return placeAirportsResponse.getBody();
    }

    private LocalDateTime postPollRequestToSkyScanner(FlightRequestPoll flightRequestPoll) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<FlightRequestPoll> httpEntity = new HttpEntity<>(flightRequestPoll, httpHeaders);

        LocalDateTime sendPollRequestDateTime = LocalDateTime.now();
        restTemplate.exchange("http://sky-scanner-service/poll", HttpMethod.POST, httpEntity, String.class).getBody();
        return sendPollRequestDateTime;
    }

}
