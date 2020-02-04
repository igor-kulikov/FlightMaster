package ua.ikulikov.flightmaster.skyscannerservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;
import ua.ikulikov.flightmaster.skyscannerservice.repositories.IFlightDataAggregatedRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DatabaseService implements IDatabaseService {
    private final IFlightDataAggregatedRepository repository;

    @Override
    public List<FlightDataAggregated> showPollResults(Long flightRequestId) {
        return repository.showPollResults(flightRequestId);
    }
}
