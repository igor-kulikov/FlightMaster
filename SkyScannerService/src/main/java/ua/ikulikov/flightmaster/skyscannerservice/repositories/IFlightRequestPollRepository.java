package ua.ikulikov.flightmaster.skyscannerservice.repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDB;

@Repository
@EntityScan(basePackageClasses = FlightRequestPollDB.class)
public interface IFlightRequestPollRepository extends JpaRepository<FlightRequestPollDB, Long> {
}
