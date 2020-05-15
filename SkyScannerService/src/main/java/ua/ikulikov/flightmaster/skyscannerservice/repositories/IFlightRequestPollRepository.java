package ua.ikulikov.flightmaster.skyscannerservice.repositories;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDto;

@Repository
@EntityScan(basePackageClasses = FlightRequestPollDto.class)
public interface IFlightRequestPollRepository extends JpaRepository<FlightRequestPollDto, Long> {
}
