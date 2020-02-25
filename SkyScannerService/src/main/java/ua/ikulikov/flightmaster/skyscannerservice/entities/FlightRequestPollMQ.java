package ua.ikulikov.flightmaster.skyscannerservice.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class FlightRequestPollMQ {
    private Long pollId;
    private Long requestId;
    private String country;
    private String currency;
    private String locale;
    private String outboundAirport;
    private String inboundAirport;
    private LocalDate outboundDate;
    private LocalDate inboundDate;
    private Integer adults;
    private Integer children;
    private Integer infants;
    private Boolean groupPricing;
}

