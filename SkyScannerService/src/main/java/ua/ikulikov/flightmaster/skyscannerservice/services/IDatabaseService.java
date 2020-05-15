package ua.ikulikov.flightmaster.skyscannerservice.services;

import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDto;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollStatus;
import ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata.FlightData;

import java.time.LocalDateTime;
import java.util.List;

public interface IDatabaseService {
    void persistDicts(FlightData flightData);
    FlightRequestPollDto persistPoll(FlightRequestPollDto poll, FlightRequestPollStatus status, LocalDateTime dt);
    FlightRequestPollDto persistPoll(FlightRequestPollDto poll, FlightRequestPollStatus status);
    List<FlightDataAggregated> showPollResults(Long flightRequestId);
}
