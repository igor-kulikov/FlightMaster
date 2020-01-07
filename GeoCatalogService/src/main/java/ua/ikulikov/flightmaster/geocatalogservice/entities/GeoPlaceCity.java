package ua.ikulikov.flightmaster.geocatalogservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties({"CountryId", "RegionId", "IataCode", "SingleAirportCity"})
public class GeoPlaceCity {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("Airports")
    private List<GeoPlaceAirport> airports;
}