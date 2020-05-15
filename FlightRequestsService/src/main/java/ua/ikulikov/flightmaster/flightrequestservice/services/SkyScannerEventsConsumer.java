package ua.ikulikov.flightmaster.flightrequestservice.services;

import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightPollStatusMQ;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPollDto;
import ua.ikulikov.flightmaster.flightrequestservice.repositories.FlightRequestPollRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SkyScannerEventsConsumer {
    private final FlightRequestPollRepository pollRepository;
    private final Logger logger = Logger.getLogger(SkyScannerEventsConsumer.class);

    @RabbitListener(queues = "${skyScannerServiceAmqp.eventsQueue}")
    @Transactional
    public void eventReceived(FlightPollStatusMQ statusMQ) {
        logger.info("FlightRequestCommand received: " + statusMQ);
        Optional<FlightRequestPollDto> flightPollOptional = pollRepository.findById(statusMQ.getPollId());
        if (!flightPollOptional.isPresent()) {
            logger.error("FlightRequestPoll with id = " + statusMQ.getPollId() + " is not found in DB");
            return;
        }

        FlightRequestPollDto flightPoll = flightPollOptional.get();
        flightPoll.setStatus(statusMQ.getPollStatus());
        flightPoll.setStatusDT(statusMQ.getPollStatusDateTime());
    }
}
