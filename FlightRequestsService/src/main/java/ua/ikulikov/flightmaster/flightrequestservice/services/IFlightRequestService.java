package ua.ikulikov.flightmaster.flightrequestservice.services;

import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestDto;

import java.util.List;
import java.util.Optional;

public interface IFlightRequestService {
    void processFlightRequest(FlightRequestDto request);
    List<FlightRequestDto> getFlightRequests();
    List<FlightRequestDto> getEnabledFlightRequests();
    FlightRequestDto addFlightRequest(FlightRequestDto flightRequestDto);
    void deleteFlightRequest(long id);
    Optional<FlightRequestDto> setEnabledFlag(long id, boolean enabled);
    Optional<FlightRequestDto> disableFlightRequest(long id);
    Optional<FlightRequestDto> enableFlightRequest(long id);
    void processFlightRequest(Long flightRequestId);
}
