package ua.ikulikov.flightmaster.flightrequestservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "skyscannerserviceamqp")
@Data
public class AmqpProperty {
    private String exchange;
    private String requestsQueue;
    private String requestRoutingKey;
    private String eventsQueue;
}
