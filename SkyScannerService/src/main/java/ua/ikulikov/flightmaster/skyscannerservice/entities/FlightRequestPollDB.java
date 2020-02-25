package ua.ikulikov.flightmaster.skyscannerservice.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata.Itinerary;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
@Table(schema = "SKY_SCANNER", name = "FLIGHT_REQUEST_POLLS")
public class FlightRequestPollDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POLL_ID")
    private Long id;

    @Column(name = "POLL_ID_EXT")
    private Long requestId;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "LOCALE")
    private String locale;

    @Column(name = "OUTBOUND_AIRPORT")
    private String outboundAirport;

    @Column(name = "INBOUND_AIRPORT")
    private String inboundAirport;

    @Column(name = "OUTBOUND_DATE")
    private LocalDate outboundDate;

    @Column(name = "INBOUND_DATE")
    private LocalDate inboundDate;

    @Column(name = "ADULTS")
    private Integer adults;

    @Column(name = "CHILDREN")
    private Integer children;

    @Column(name = "INFANTS")
    private Integer infants;

    @Column(name = "GROUP_PRICING")
    private Boolean groupPricing;

    @Column(name = "POLL_STATUS")
    @Enumerated(EnumType.STRING)
    private FlightRequestPollStatus flightRequestPollStatus;

    @Column(name = "POLL_STATUS_DT")
    private LocalDateTime pollStatusDateTime;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(schema = "SKY_SCANNER", name = "POLL_ITINERARIES",
            joinColumns = {@JoinColumn(name = "POLL_ID")},
            inverseJoinColumns = {@JoinColumn(name = "ITINERARY_ID")})
    private Set<Itinerary> itineraries;

    public FlightRequestPollDB(FlightRequestPollMQ pollMQ) {
        this.id = pollMQ.getPollId();
        this.requestId = pollMQ.getRequestId();
        this.country = pollMQ.getCountry();
        this.currency = pollMQ.getCurrency();
        this.locale = pollMQ.getLocale();
        this.outboundAirport = pollMQ.getOutboundAirport();
        this.inboundAirport = pollMQ.getInboundAirport();
        this.outboundDate = pollMQ.getOutboundDate();
        this.inboundDate = pollMQ.getInboundDate();
        this.adults = pollMQ.getAdults();
        this.children = pollMQ.getChildren();
        this.infants = pollMQ.getInfants();
        this.groupPricing = pollMQ.getGroupPricing();
    }
}