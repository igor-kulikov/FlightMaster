package ua.ikulikov.flightmaster.flightrequestservice.repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPollDto;

@Repository
@EntityScan(basePackageClasses = FlightRequestPollDto.class)
public interface FlightRequestPollRepository extends JpaRepository<FlightRequestPollDto, Long> {
}
