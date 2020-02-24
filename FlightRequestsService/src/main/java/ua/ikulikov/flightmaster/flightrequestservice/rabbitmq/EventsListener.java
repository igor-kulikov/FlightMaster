package ua.ikulikov.flightmaster.flightrequestservice.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightPollStatusMQ;
import ua.ikulikov.flightmaster.flightrequestservice.entities.FlightRequestPollDB;
import ua.ikulikov.flightmaster.flightrequestservice.repositories.FlightRequestPollRepository;

import javax.transaction.Transactional;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventsListener {
    private final FlightRequestPollRepository pollRepository;

    @RabbitListener(queues = "${skyScannerServiceAmqp.eventsQueue}")
    @Transactional
    public void eventReceived(FlightPollStatusMQ statusMQ) {
        System.out.println("RabbitMQ event message received:" + statusMQ);
        Optional<FlightRequestPollDB> flightPollOptional = pollRepository.findById(statusMQ.getPollId());
        if (!flightPollOptional.isPresent())
            return;

        FlightRequestPollDB flightPoll = flightPollOptional.get();
        flightPoll.setStatus(statusMQ.getPollStatus());
        flightPoll.setStatusDT(statusMQ.getPollStatusDateTime());
    }
}
