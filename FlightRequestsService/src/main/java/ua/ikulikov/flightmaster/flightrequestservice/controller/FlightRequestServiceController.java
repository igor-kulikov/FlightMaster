package ua.ikulikov.flightmaster.flightrequestservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestDto;
import ua.ikulikov.flightmaster.flightrequestservice.services.IFlightRequestService;

import java.util.List;
import java.util.Optional;

@RestController
public class FlightRequestServiceController {
    @Autowired
    private IFlightRequestService flightRequestService;

	@GetMapping(value="/")
    public List<FlightRequestDto> getFlightRequests() {
        return flightRequestService.getFlightRequests();
    }

    @GetMapping(value="/enabled")
    public List<FlightRequestDto> getEnabledFlightRequests() {
        return flightRequestService.getEnabledFlightRequests();
    }

    @PostMapping("/add")
    public FlightRequestDto addRequest(@RequestBody FlightRequestDto flightRequestDto) {
        return flightRequestService.addFlightRequest(flightRequestDto);
    }

    @DeleteMapping("/delete/{flightRequestId}")
    public void deleteFlightRequest(@PathVariable Long flightRequestId) {
        flightRequestService.deleteFlightRequest(flightRequestId);
    }

    @PutMapping("/enable/{flightRequestId}")
    public ResponseEntity enableFlightRequest(@PathVariable Long flightRequestId) {
        Optional<FlightRequestDto> flightRequestOptional = flightRequestService.setEnabledFlag(flightRequestId, true);
        return prepareResponse(flightRequestOptional, flightRequestId);
    }

    @PutMapping("/disable/{flightRequestId}")
    public ResponseEntity disableFlightRequest(@PathVariable Long flightRequestId) {
        Optional<FlightRequestDto> flightRequestOptional = flightRequestService.setEnabledFlag(flightRequestId, false);
        return prepareResponse(flightRequestOptional, flightRequestId);
    }

    @PostMapping("/poll/{flightRequestId}")
    public void processFlightRequest(@PathVariable Long flightRequestId) {
	    flightRequestService.processFlightRequest(flightRequestId);
    }

    private ResponseEntity prepareResponse(Optional<FlightRequestDto> flightRequestOptional, Long flightRequestId) {
        if (!flightRequestOptional.isPresent())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Unable to find Flight Request with ID = " + flightRequestId);
        else
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(flightRequestOptional.get());
    }
}
