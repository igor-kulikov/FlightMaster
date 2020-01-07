package ua.ikulikov.flightmaster.geocatalogservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class GeoCatalog {
    @JsonProperty("Continents")
    private List<GeoPlaceContinent> continents;

    public Set<String> getChildAirports(String place) {
        boolean isFoundContinent = false;
        boolean isFoundCountry = false;
        boolean isFoundCity = false;
        boolean isFoundAirport = false;
        Set<String> airports = new HashSet<>();

        if (!place.isEmpty())
            for (GeoPlaceContinent continent : continents) {
                if (continent.getName().equals(place))
                    isFoundContinent = true;
                for (GeoPlaceCountry country : continent.getCountries()) {
                    if (country.getId().equals(place))
                        isFoundCountry = true;
                    for (GeoPlaceCity city : country.getCities()) {
                        if (city.getId().equals(place))
                            isFoundCity = true;
                        for (GeoPlaceAirport airport : city.getAirports()) {
                            if (airport.getId().equals(place))
                                isFoundAirport = true;
                            if (isFoundContinent || isFoundCountry || isFoundCity || isFoundAirport)
                                airports.add(airport.getId());
                            isFoundAirport = false;
                        }
                        isFoundCity = false;
                    }
                    isFoundCountry = false;
                }
                isFoundContinent = false;
            }
        else
            airports.add("");
        return airports;
    }
}