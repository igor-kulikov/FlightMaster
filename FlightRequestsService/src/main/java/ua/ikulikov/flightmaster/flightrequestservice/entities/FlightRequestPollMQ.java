package ua.ikulikov.flightmaster.flightrequestservice.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FlightRequestPollMQ {
    private Long pollId;
    private Long requestId;
    private String country;
    private String currency;
    private String locale;
    private String outboundAirport;
    private String inboundAirport;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate outboundDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate inboundDate;

    private Integer adults;
    private Integer children;
    private Integer infants;
    private Boolean groupPricing;

    public FlightRequestPollMQ(FlightRequestPollDB poll) {
        this.pollId = poll.getId();
        this.requestId = poll.getRequestId();
        this.country = poll.getCountry();
        this.currency = poll.getCurrency();
        this.locale = poll.getLocale();
        this.outboundAirport = poll.getOutboundAirport();
        this.inboundAirport = poll.getInboundAirport();
        this.outboundDate = poll.getOutboundDate();
        this.inboundDate = poll.getInboundDate();
        this.adults = poll.getAdults();
        this.children = poll.getChildren();
        this.infants = poll.getInfants();
        this.groupPricing = poll.getGroupPricing();
    }
}
