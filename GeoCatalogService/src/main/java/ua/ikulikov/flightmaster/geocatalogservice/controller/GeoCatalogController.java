package ua.ikulikov.flightmaster.geocatalogservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.ikulikov.flightmaster.geocatalogservice.services.GeoCatalogService;

import java.util.Set;

@RestController
public class GeoCatalogController {
    @Autowired
    private GeoCatalogService geoCatalogService;

	@GetMapping(value="/getChild/{place}")
    public Set<String> getChildAirports(@PathVariable String place) {
        return geoCatalogService.getChildAirports(place);
    }
}
