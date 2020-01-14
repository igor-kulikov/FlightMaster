package ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"legId"})
@JsonIgnoreProperties({"SegmentIds", "FlightNumbers", "OperatingCarriers", "Directionality"})
@Table(schema = "SKY_SCANNER", name = "LEGS")
@Entity
public class Leg {
    @JsonProperty("Id")
    @Id
    @Column(name = "LEG_ID")
    private String legId;

    @JsonProperty("JourneyMode")
    @Column(name = "JOURNEY_MODE")
    private String journeyMode;

    @JsonProperty("OriginStation")
    @Column(name = "ORIGIN_STATION_PLACE_ID")
    private int originStationPlaceId;

    @JsonProperty("Departure")
    @Column(name = "DEPARTURE_DT")
    private LocalDateTime departureDateTime;

    @JsonProperty("DestinationStation")
    @Column(name = "DESTINATION_STATION_PLACE_ID")
    private int destinationStationPlaceId;

    @JsonProperty("Arrival")
    @Column(name = "ARRIVAL_DT")
    private LocalDateTime arrivalDateTime;

    @JsonProperty("Duration")
    @Column(name = "DURATION_MM")
    private int duration;

    @JsonProperty("Carriers")
    @ElementCollection
    @CollectionTable(schema = "SKY_SCANNER", name = "LEG_CARRIERS", joinColumns = @JoinColumn(name = "LEG_ID"))
    @Column(name = "CARRIER_ID")
    private Set<Integer> carrierIds;

    @JsonProperty("Stops")
    @ElementCollection
    @CollectionTable(schema = "SKY_SCANNER", name = "LEG_STOPS", joinColumns = @JoinColumn(name = "LEG_ID"))
    @Column(name = "STOP_PLACE_ID")
    private Set<Integer> stopPlaceIds;
}
