package ua.ikulikov.flightmaster.skyscannerservice.services;

import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDto;

public interface ISkyScannerService {
    void proceedFlightPollRequest(FlightRequestPollDto poll);
}
