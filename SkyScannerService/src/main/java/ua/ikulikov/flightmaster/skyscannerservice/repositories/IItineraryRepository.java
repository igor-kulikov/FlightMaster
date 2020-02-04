package ua.ikulikov.flightmaster.skyscannerservice.repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.skyscannerservice.entities.flightdata.Itinerary;

@Repository
@EntityScan(basePackageClasses = Itinerary.class)
public interface IItineraryRepository extends JpaRepository<Itinerary, Long> {
}
