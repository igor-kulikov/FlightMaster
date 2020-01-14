package ua.ikulikov.flightmaster.skyscannerservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class RestConfig {
    @Bean
//    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate rest = new RestTemplate();
        MyRestResponseErrorHandler myRestResponseErrorHandler = new MyRestResponseErrorHandler();
        rest.setErrorHandler(myRestResponseErrorHandler);
        return rest;
    }
}

/**
 * Http responses related to client side errors (status code = 4xx) requires investigation and will
 * interrupt program execution
 * <br>
 * Exception: response = 429 (too many requests)
 * SkyScanner occasionally sends Http response = 429 (too many requests)
 * even if number of client requests is below then allowed 100 per minute, so we suppose the code as correct
 * and wouldn't interrupt polling process
 */
class MyRestResponseErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR && response.getStatusCode().value() != 429;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        throw new HttpClientErrorException(response.getStatusCode());
    }
}
