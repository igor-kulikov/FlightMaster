package ua.ikulikov.flightmaster.skyscannerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPoll;
import ua.ikulikov.flightmaster.skyscannerservice.services.IDatabaseService;
import ua.ikulikov.flightmaster.skyscannerservice.services.ISkyScannerService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkyScannerServiceController {
    private final ISkyScannerService skyScannerService;
    private final IDatabaseService databaseService;

    @PostMapping("/poll")
    public ResponseEntity<String> pollSkyScanner(@RequestBody FlightRequestPoll flightRequestPoll) {
        LocalDateTime requestReceivedDateTime = LocalDateTime.now();
        skyScannerService.proceedFlightPollRequest(flightRequestPoll, requestReceivedDateTime);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Request is accepted");
    }

    @GetMapping("/show/{flightRequestId}")
    public List<FlightDataAggregated> showPollResults(@PathVariable Long flightRequestId) {
        return databaseService.showPollResults(flightRequestId);
    }
}
