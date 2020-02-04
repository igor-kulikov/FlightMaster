package ua.ikulikov.flightmaster.skyscannerservice.repositories;

import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;

import java.util.List;

@Repository
public interface IFlightDataAggregatedRepository {
    List<FlightDataAggregated> showPollResults(Long id);
}