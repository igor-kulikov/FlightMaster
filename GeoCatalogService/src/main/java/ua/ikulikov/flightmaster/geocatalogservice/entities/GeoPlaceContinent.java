package ua.ikulikov.flightmaster.geocatalogservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties("Id")
public class GeoPlaceContinent {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Countries")
    private List<GeoPlaceCountry> countries;
}