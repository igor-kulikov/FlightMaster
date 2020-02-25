package ua.ikulikov.flightmaster.skyscannerservice.services;

import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDB;

public interface ISkyScannerService {
    void proceedFlightPollRequest(FlightRequestPollDB poll);
}
