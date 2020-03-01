package ua.ikulikov.flightmaster.skyscannerservice.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightPollStatusMQ {
    private final Long pollId;
    private final FlightRequestPollStatus pollStatus;
    private final LocalDateTime pollStatusDateTime;
}
