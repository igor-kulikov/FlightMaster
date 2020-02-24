package ua.ikulikov.flightmaster.flightrequestservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.ikulikov.flightmaster.flightrequestservice.config.AmqpProperty;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestDB;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPollDB;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPollMQ;
import ua.ikulikov.flightmaster.flightrequestservice.entities.PollStatus;
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
    private final FlightRequestRepository requestRepository;
    private final FlightRequestPollRepository pollRepository;
    private final AmqpTemplate amqpRequestsPublisher;
    private final AmqpProperty amqpProps;

    @Override
    public void processFlightRequest(FlightRequestDB request) {
    }

    @Override
    public List<FlightRequestDB> getFlightRequests() {
        return requestRepository.findAll();
    }

    @Override
    public List<FlightRequestDB> getEnabledFlightRequests() {
        return getFlightRequests()
                .stream()
                .filter(FlightRequestDB::getEnabledFlag)
                .collect(Collectors.toList());
    }

    @Override
    public FlightRequestDB addFlightRequest(FlightRequestDB flightRequestDB) {
        return requestRepository.saveAndFlush(flightRequestDB);
    }

    @Override
    public void deleteFlightRequest(long id) {
        requestRepository.deleteById(id);
    }

    @Override
    public Optional<FlightRequestDB> disableFlightRequest(long id) {
        return setEnabledFlag(id, false);
    }

    @Override
    public Optional<FlightRequestDB> enableFlightRequest(long id) {
        return setEnabledFlag(id, true);
    }

    @Override
    public Optional<FlightRequestDB> setEnabledFlag(long id, boolean enabled) {
        Optional<FlightRequestDB> flightRequestOptional = requestRepository.findById(id);
        if (flightRequestOptional.isPresent()) {
            FlightRequestDB flightRequestDB = flightRequestOptional.get();
            flightRequestDB.setEnabledFlag(enabled);
            return Optional.of(requestRepository.save(flightRequestDB));
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
        Optional<FlightRequestDB> flightRequestOptional = requestRepository.findById(flightRequestId);
        if (!flightRequestOptional.isPresent()) {
            return;
        }

        //get airports located in origin-place and destination-place
        FlightRequestDB flightRequestDB = flightRequestOptional.get();
        Set<String> originAirports = geoCatalogService.getChildAirportsInPlace(flightRequestDB.getOriginPlace());
        Set<String> destinationAirports = geoCatalogService.getChildAirportsInPlace(flightRequestDB.getDestinationPlace());

        BiFunction<LocalDate, Integer, LocalDate> shiftLocalDateFunc =  (date, days) -> (date == null) ? null : date.plusDays(days);
        for(String originAirport : originAirports)
            for (String destinationAirport : destinationAirports)
                for (int shiftDays = 0; shiftDays <= flightRequestDB.getSerialPollPeriod(); shiftDays++) {
                    LocalDate shiftedOutboundDate = shiftLocalDateFunc.apply(flightRequestDB.getOutboundDate(), shiftDays);
                    LocalDate shiftedInboundDate = shiftLocalDateFunc.apply(flightRequestDB.getInboundDate(), shiftDays);

                    FlightRequestPollDB pollDB = new FlightRequestPollDB(flightRequestDB,
                            originAirport, destinationAirport, shiftedOutboundDate, shiftedInboundDate,
                            PollStatus.NEW, LocalDateTime.now());
                    pollRepository.save(pollDB);

                    // send message to request queue of SkyScanner RabbitMQ exchange
                    FlightRequestPollMQ pollMQ = new FlightRequestPollMQ(pollDB);
                    amqpRequestsPublisher.convertAndSend(amqpProps.getExchange(), amqpProps.getRequestRoutingKey(), pollMQ);
                }
    }
}
