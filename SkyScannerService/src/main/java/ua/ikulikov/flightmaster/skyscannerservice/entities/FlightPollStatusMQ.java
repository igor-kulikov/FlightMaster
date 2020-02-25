package ua.ikulikov.flightmaster.skyscannerservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class FlightPollStatusMQ {
    private final Long pollId;
    private final FlightRequestPollStatus flightRequestPollStatus;
    private final LocalDateTime pollStatusDateTime;
}
