package ua.ikulikov.flightmaster.flightrequestservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(schema = "FLIGHT_REQUEST_SERVICE", name = "FLIGHT_REQUEST_POLLS")
public class FlightRequestPollDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POLL_ID", unique = true, nullable = false)
    private Long id;

    @Transient
    private FlightRequestDto request;

    @Transient
    private String country;

    @Transient
    private String currency;

    @Transient
    private String locale;

    @Column(name = "REQUEST_ID")
    private Long requestId;

    @Column(name = "OUTBOUND_AIRPORT")
    private String outboundAirport;

    @Column(name = "INBOUND_AIRPORT")
    private String inboundAirport;

    @Column(name = "OUTBOUND_DATE")
    private LocalDate outboundDate;

    @Column(name = "INBOUND_DATE")
    private LocalDate inboundDate;

    @Transient
    private Integer adults;

    @Transient
    private Integer children;

    @Transient
    private Integer infants;

    @Transient
    private Boolean groupPricing;

    @Column(name = "POLL_STATUS")
    @Enumerated(EnumType.STRING)
    private PollStatus status;

    @Column(name = "POLL_STATUS_DT")
    private LocalDateTime statusDT;

    public FlightRequestPollDto(FlightRequestDto request, String outboundAirport, String inboundAirport, LocalDate outboundDate,
                                LocalDate inboundDate, PollStatus status, LocalDateTime statusDT) {
        this.request = request;
        this.requestId = request.getId();
        this.country = request.getCountry();
        this.currency = request.getCurrency();
        this.locale = request.getLocale();
        this.outboundAirport = outboundAirport;
        this.inboundAirport = inboundAirport;
        this.outboundDate = outboundDate;
        this.inboundDate = inboundDate;
        this.adults = request.getAdults();
        this.children = request.getChildren();
        this.infants = request.getInfants();
        this.groupPricing = request.getGroupPricing();
        this.status = status;
        this.statusDT = statusDT;
    }
}
