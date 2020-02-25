package ua.ikulikov.flightmaster.skyscannerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightDataAggregated;
import ua.ikulikov.flightmaster.skyscannerservice.services.IDatabaseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkyScannerServiceController {
    private final IDatabaseService databaseService;

    @GetMapping("/show/{flightRequestId}")
    public List<FlightDataAggregated> showPollResults(@PathVariable Long flightRequestId) {
        return databaseService.showPollResults(flightRequestId);
    }
}
