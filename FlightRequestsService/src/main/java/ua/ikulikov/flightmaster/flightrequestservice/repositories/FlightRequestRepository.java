package ua.ikulikov.flightmaster.flightrequestservice.repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestDB;

@Repository
@EntityScan(basePackageClasses = FlightRequestDB.class)
public interface FlightRequestRepository extends JpaRepository<FlightRequestDB, Long> {
}