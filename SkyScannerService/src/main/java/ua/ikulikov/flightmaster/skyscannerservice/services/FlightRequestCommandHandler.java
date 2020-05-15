package ua.ikulikov.flightmaster.skyscannerservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDto;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollMQ;

@Component
@RequiredArgsConstructor
public class FlightRequestCommandHandler {
    private final ISkyScannerService skyScannerService;
    private final Logger logger = Logger.getLogger(FlightRequestCommandHandler.class);

    @RabbitListener(queues = "${skyScannerServiceAmqp.requestsQueue}")
    @Transactional
    public void requestReceived(FlightRequestPollMQ pollMQ) {
        FlightRequestPollDto pollDB = new FlightRequestPollDto(pollMQ);
        skyScannerService.proceedFlightPollRequest(pollDB);
        logger.info("FlightRequestCommand received: " + pollMQ);
    }
}
