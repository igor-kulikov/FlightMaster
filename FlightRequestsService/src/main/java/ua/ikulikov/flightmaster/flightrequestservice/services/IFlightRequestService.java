package ua.ikulikov.flightmaster.flightrequestservice.services;

import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestDB;

import java.util.List;
import java.util.Optional;

public interface IFlightRequestService {
    void processFlightRequest(FlightRequestDB request);
    List<FlightRequestDB> getFlightRequests();
    List<FlightRequestDB> getEnabledFlightRequests();
    FlightRequestDB addFlightRequest(FlightRequestDB flightRequestDB);
    void deleteFlightRequest(long id);
    Optional<FlightRequestDB> setEnabledFlag(long id, boolean enabled);
    Optional<FlightRequestDB> disableFlightRequest(long id);
    Optional<FlightRequestDB> enableFlightRequest(long id);
    void processFlightRequest(Long flightRequestId);
}
