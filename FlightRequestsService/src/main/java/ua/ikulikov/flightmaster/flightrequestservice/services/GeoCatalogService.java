package ua.ikulikov.flightmaster.flightrequestservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class GeoCatalogService implements IGeoCatalogService {
    @Value("${flightRequest.geoCatalogService.url}")
    private String url;

    @Value("${flightRequest.geoCatalogService.childAirportsEndPoint}")
    private String childAirportsEndPoint;


    private final RestTemplate restTemplate;

    @Cacheable(value = "childAirports")
    public Set<String> getChildAirportsInPlace(String place) {
        ResponseEntity<Set<String>> placeAirportsResponse = restTemplate.exchange(
                url + childAirportsEndPoint + place, HttpMethod.GET, null,
                new ParameterizedTypeReference<Set<String>>() {
                });

        return placeAirportsResponse.getBody();
    }
}
