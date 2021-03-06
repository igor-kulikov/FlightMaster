package ua.ikulikov.flightmaster.skyscannerservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RabbitMQConfig {
    private final AmqpPropertyConfig amqpProps;

    @Bean
    public DirectExchange skyScannerAmqpExchange() {
        return new DirectExchange(amqpProps.getExchange());
    }

    @Bean
    @Qualifier("requestsQueue")
    public Queue skyScannerRequestsQueue() {
        return QueueBuilder.durable(amqpProps.getRequestsQueue()).build();
    }

    @Bean
    @Qualifier("eventsQueue")
    public Queue skyScannerEventsQueue() {
        return QueueBuilder.durable(amqpProps.getEventsQueue()).build();
    }

    @Bean
    public Binding skyScannerEventsBinding(DirectExchange exchange, @Qualifier("eventsQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(amqpProps.getEventRoutingKey());
    }


    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        return simpleMessageListenerContainer;
    }

    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
