package ua.ikulikov.flightmaster.flightrequestservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequest;
import ua.ikulikov.flightmaster.flightrequestservice.repositories.JpaFlightRequestRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightRequestService implements IFlightRequestService {
    private final JpaFlightRequestRepository repository;

    @Override
    public void processFlightRequest(FlightRequest request) {

    }

    @Override
    public List<FlightRequest> getFlightRequests() {
        return repository.findAll();
    }

    @Override
    public List<FlightRequest> getEnabledFlightRequests() {
        return getFlightRequests()
                .stream()
                .filter(FlightRequest::isEnabledFlag)
                .collect(Collectors.toList());
    }

    @Override
    public FlightRequest addFlightRequest(FlightRequest flightRequest) {
        return repository.saveAndFlush(flightRequest);
    }

    @Override
    public void deleteFlightRequest(long id) {
        repository.deleteById(id);
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
        Optional<FlightRequest> flightRequestOptional = repository.findById(id);
        if (flightRequestOptional.isPresent()) {
            FlightRequest flightRequest = flightRequestOptional.get();
            flightRequest.setEnabledFlag(enabled);
            return Optional.of(repository.save(flightRequest));
        }
        return Optional.empty();
    }
}
