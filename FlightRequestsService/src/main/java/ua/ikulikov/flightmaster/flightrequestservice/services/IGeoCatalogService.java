package ua.ikulikov.flightmaster.flightrequestservice.services;

import java.util.Set;

public interface IGeoCatalogService {
    Set<String> getChildAirportsInPlace(String place);
}
