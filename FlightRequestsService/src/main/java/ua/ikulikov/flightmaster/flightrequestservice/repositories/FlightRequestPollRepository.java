package ua.ikulikov.flightmaster.flightrequestservice.repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPoll;

@Repository
@EntityScan(basePackageClasses = FlightRequestPoll.class)
public interface FlightRequestPollRepository extends JpaRepository<FlightRequestPoll, Long> {
}
