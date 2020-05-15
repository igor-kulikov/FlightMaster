package ua.ikulikov.flightmaster.skyscannerservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "skyscannerserviceamqp")
@Data
public class AmqpPropertyConfig {
    private String exchange;
    private String requestsQueue;
    private String eventsQueue;
    private String eventRoutingKey;
}
