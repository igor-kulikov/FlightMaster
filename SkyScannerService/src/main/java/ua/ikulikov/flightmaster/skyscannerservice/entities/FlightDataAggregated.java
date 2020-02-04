package ua.ikulikov.flightmaster.skyscannerservice.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Table(schema = "SKY_SCANNER", name = "FLIGHT_DATA_AGGR")
@Entity
public class FlightDataAggregated {
    @Id
    @Column(name = "POLL_ID")
    private Long id;

    @Column(name = "POLL_ID_EXT")
    private Long extId;

    @Column(name = "OUTBOUND_AIRPORT")
    private String outboundAirport;

    @Column(name = "INBOUND_AIRPORT")
    private String inboundAirport;

    @Column(name = "OUTBOUND_DATE")
    private LocalDateTime outboundDate;

    @Column(name = "INBOUND_DATE")
    private LocalDateTime inboundDate;

    @Column(name = "DEPARTURE_DT")
    private String departureDt;

    @Column(name = "ARRIVAL_DT")
    private String arrivalDt;

    @Column(name = "DURATION_MM")
    private String durationMm;

    @Column(name = "CARRIERS")
    private String carriers;

    @Column(name = "BACK_DEPARTURE_DT")
    private LocalDateTime backDepartureDt;

    @Column(name = "BACK_ARRIVAL_DT")
    private LocalDateTime backArrivalDt;

    @Column(name = "BACK_DURATION_MM")
    private String backDurationMm;

    @Column(name = "BACK_CARRIERS")
    private String backCarriers;

    @Column(name = "AGENTS")
    private String agents;

    @Column(name = "PRICE")
    private String price;

    @Column(name = "DEEPLINK_URL")
    private String deeplinkUrl;

    @Column(name = "QUOTE_AGE_MM")
    private String quoteAgeMm;

    @Column(name = "POLL_COMPLETE_DT")
    private LocalDateTime pollCompleteDt;
}
