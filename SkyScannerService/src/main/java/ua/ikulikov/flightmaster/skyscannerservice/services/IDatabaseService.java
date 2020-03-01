package ua.ikulikov.flightmaster.skyscannerservice.services;

import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDB;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollStatus;
import ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata.FlightData;

import java.time.LocalDateTime;
import java.util.List;

public interface IDatabaseService {
    void persistDicts(FlightData flightData);
    FlightRequestPollDB persistPoll(FlightRequestPollDB poll, FlightRequestPollStatus status, LocalDateTime dt);
    FlightRequestPollDB persistPoll(FlightRequestPollDB poll, FlightRequestPollStatus status);
    List<FlightDataAggregated> showPollResults(Long flightRequestId);
}
