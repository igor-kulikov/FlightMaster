package ua.ikulikov.flightmaster.flightrequestservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "FLIGHT_REQUESTS")
public class FlightRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    private Long id;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "LOCALE")
    private String locale;

    @Column(name = "ORIGIN_PLACE")
    private String originPlace;

    @Column(name = "DESTINATION_PLACE")
    private String destinationPlace;

    @Column(name = "OUTBOUND_DATE")
    private LocalDate outboundDate;

    @Column(name = "INBOUND_DATE")
    private LocalDate inboundDate;

    @Column(name = "ADULTS")
    private int adults;

    @Column(name = "CHILDREN")
    private int children;

    @Column(name = "INFANTS")
    private int infants;

    @Column(name = "GROUP_PRICING")
    private boolean groupPricing;

    @Column(name = "SERIAL_POLL_PERIOD")
    private int serialPollPeriod;

    @Column(name = "ENABLED_FLAG")
    private boolean enabledFlag;
}