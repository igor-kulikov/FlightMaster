package ua.ikulikov.flightmaster.flightrequestservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequest;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPoll;
import ua.ikulikov.flightmaster.flightrequestservice.repositories.FlightRequestPollRepository;
import ua.ikulikov.flightmaster.flightrequestservice.repositories.FlightRequestRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightRequestService implements IFlightRequestService {
    @Value("${flightRequest.skyScannerService.url}")
    private String url;

    @Value("${flightRequest.skyScannerService.pollEndPoint}")
    private String pollEndPoint;

    private final GeoCatalogService geoCatalogService;
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

    /**
     * Processes flight request with flexible configuration of:
     * <ul>
     *     <li> location, e.g. KBP->GR: Kiev Borispol airport -> any airport in Greece</li>
     *     <li> shifting window for flight dates, e.g. [2020-01-01; 2020-01-05], [2020-01-02; 2020-01-06], etc.</li>
     * </ul>
     */
    @Override
    @Transactional
    public void processFlightRequest(Long flightRequestId) {
        //get flightRequest object by Id
        Optional<FlightRequest> flightRequestOptional = flightRequestRepository.findById(flightRequestId);
        if (!flightRequestOptional.isPresent()) {
            return;
        }

        //get airports located in origin-place and destination-place
        FlightRequest flightRequest = flightRequestOptional.get();
        Set<String> originAirports = geoCatalogService.getChildAirportsInPlace(flightRequest.getOriginPlace());
        Set<String> destinationAirports = geoCatalogService.getChildAirportsInPlace(flightRequest.getDestinationPlace());

        BiFunction<LocalDate, Integer, LocalDate> shiftLocalDateFunc =  (date, days) -> (date == null) ? null : date.plusDays(days);
        for(String originAirport : originAirports)
            for (String destinationAirport : destinationAirports)
                for (int shiftDays = 0; shiftDays <= flightRequest.getSerialPollPeriod(); shiftDays++) {
                    LocalDate shiftedOutboundDate = shiftLocalDateFunc.apply(flightRequest.getOutboundDate(), shiftDays);
                    LocalDate shiftedInboundDate = shiftLocalDateFunc.apply(flightRequest.getInboundDate(), shiftDays);

                    FlightRequestPoll flightRequestPoll = new FlightRequestPoll(flightRequest,
                            originAirport, destinationAirport, shiftedOutboundDate, shiftedInboundDate);

                    // poll SkyScanner service for flights between airports
                    LocalDateTime sendPollRequestDateTime = postPollRequestToSkyScanner(flightRequestPoll);
                    flightRequestPoll.setPollDateTime(sendPollRequestDateTime);
                }
    }

    private LocalDateTime postPollRequestToSkyScanner(FlightRequestPoll flightRequestPoll) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity<FlightRequestPoll> httpEntity = new HttpEntity<>(flightRequestPoll, httpHeaders);

        LocalDateTime sendPollRequestDateTime = LocalDateTime.now();
        restTemplate.exchange(url + pollEndPoint, HttpMethod.POST, httpEntity, String.class).getBody();
        return sendPollRequestDateTime;
    }
}
