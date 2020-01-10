package ua.ikulikov.flightmaster.flightrequestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FlightRequestsServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(FlightRequestsServiceApp.class, args);
	}
}
