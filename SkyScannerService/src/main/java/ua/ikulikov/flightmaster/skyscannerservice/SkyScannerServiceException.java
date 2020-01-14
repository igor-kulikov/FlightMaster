package ua.ikulikov.flightmaster.skyscannerservice;

import lombok.Data;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollStatus;

@Data
public class SkyScannerServiceException extends Exception {
    private FlightRequestPollStatus flightRequestPollStatus;

    public SkyScannerServiceException(FlightRequestPollStatus flightRequestPollStatus, String message) {
        super(message);
        this.flightRequestPollStatus = flightRequestPollStatus;
    }
}
