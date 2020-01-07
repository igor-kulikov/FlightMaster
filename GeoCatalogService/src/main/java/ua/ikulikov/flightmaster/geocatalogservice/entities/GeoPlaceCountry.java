package ua.ikulikov.flightmaster.geocatalogservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties({"Regions", "LanguageId", "CurrencyId"})
public class GeoPlaceCountry {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Cities")
    private List<GeoPlaceCity> cities;
}