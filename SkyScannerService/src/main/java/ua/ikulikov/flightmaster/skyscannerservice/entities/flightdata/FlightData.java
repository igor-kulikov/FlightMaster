package ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonIgnoreProperties({"Query", "SessionKey", "Segments", "Currencies"})
public class FlightData {
    @JsonProperty("Agents")
    private List<Agent> agents;

    @JsonProperty("Carriers")
    private List<Carrier> carriers;

    @JsonProperty("Places")
    private List<Place> places;

    @JsonProperty("Legs")
    private List<Leg> legs;

    @JsonProperty("Itineraries")
    private Set<Itinerary> itineraries;

    @JsonProperty("Status")
    private String status;
}
