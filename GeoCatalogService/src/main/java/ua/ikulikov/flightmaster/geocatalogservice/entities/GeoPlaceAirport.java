package ua.ikulikov.flightmaster.geocatalogservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties({"CountryId", "CityId", "RegionId"})
public class GeoPlaceAirport {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Location")
    private String location;
}