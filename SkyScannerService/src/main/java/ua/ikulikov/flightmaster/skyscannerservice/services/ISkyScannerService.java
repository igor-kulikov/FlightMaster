package ua.ikulikov.flightmaster.skyscannerservice.services;

import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPoll;

import java.time.LocalDateTime;

public interface ISkyScannerService {
    void proceedFlightPollRequest(FlightRequestPoll flightRequestPoll, LocalDateTime requestReceivedDateTime);
}
