package ua.ikulikov.flightmaster.flightrequestservice.services;

import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequest;

import java.util.List;
import java.util.Optional;

public interface IFlightRequestService {
    void processFlightRequest(FlightRequest request);
    List<FlightRequest> getFlightRequests();
    List<FlightRequest> getEnabledFlightRequests();
    FlightRequest addFlightRequest(FlightRequest flightRequest);
    void deleteFlightRequest(long id);
    Optional<FlightRequest> setEnabledFlag(long id, boolean enabled);
    Optional<FlightRequest> disableFlightRequest(long id);
    Optional<FlightRequest> enableFlightRequest(long id);
    void processFlightRequest(Long flightRequestId);
}
