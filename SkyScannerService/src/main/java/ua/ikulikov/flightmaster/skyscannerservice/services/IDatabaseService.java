package ua.ikulikov.flightmaster.skyscannerservice.services;

import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;

import java.util.List;

public interface IDatabaseService {
    List<FlightDataAggregated> showPollResults(Long flightRequestId);
}
