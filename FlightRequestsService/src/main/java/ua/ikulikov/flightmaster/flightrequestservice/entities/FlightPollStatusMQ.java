package ua.ikulikov.flightmaster.flightrequestservice.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FlightPollStatusMQ {
    private final Long pollId;
    private final PollStatus pollStatus;
    private final LocalDateTime pollStatusDateTime;
}
