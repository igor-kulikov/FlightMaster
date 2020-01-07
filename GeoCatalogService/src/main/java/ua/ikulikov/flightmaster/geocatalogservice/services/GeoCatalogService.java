package ua.ikulikov.flightmaster.geocatalogservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import ua.ikulikov.flightmaster.geocatalogservice.ApplicationException;
import ua.ikulikov.flightmaster.geocatalogservice.entities.GeoCatalog;
import ua.ikulikov.flightmaster.geocatalogservice.repositories.GeoCatalogRepository;
import ua.ikulikov.flightmaster.geocatalogservice.utils.Utils;

import java.util.Set;

@Service
public class GeoCatalogService implements CommandLineRunner {
    @Autowired
    private GeoCatalogRepository repository;

    private GeoCatalog geoCatalog;

    public GeoCatalogService(@Value("${geocatalog.file_location}") String geoCatalogFileLocation) throws ApplicationException {
        geoCatalog = Utils.deserializeJson(geoCatalogFileLocation, GeoCatalog.class);
    }

    @Override
    public void run(String... args) throws Exception {
        repository.saveGeoCatalog(geoCatalog);
    }

    public Set<String> getChildAirports(String place) {
        return repository.getChildAirports(place);
    }
}
