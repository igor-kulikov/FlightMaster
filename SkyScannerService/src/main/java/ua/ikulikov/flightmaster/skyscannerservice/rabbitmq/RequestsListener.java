package ua.ikulikov.flightmaster.skyscannerservice.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollDB;
import ua.ikulikov.flightmaster.skyscannerservice.entities.FlightRequestPollMQ;
import ua.ikulikov.flightmaster.skyscannerservice.services.ISkyScannerService;

@Component
@RequiredArgsConstructor
public class RequestsListener {
    private final ISkyScannerService skyScannerService;
    private final Logger logger = Logger.getLogger(RequestsListener.class);

    @RabbitListener(queues = "${skyScannerServiceAmqp.requestsQueue}")
    @Transactional
    public void requestReceived(FlightRequestPollMQ pollMQ) {
        FlightRequestPollDB pollDB = new FlightRequestPollDB(pollMQ);
        skyScannerService.proceedFlightPollRequest(pollDB);
        logger.info("RabbitMQ request message received: " + pollMQ);
    }
}
