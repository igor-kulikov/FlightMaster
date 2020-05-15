package ua.ikulikov.flightmaster.geocatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GeoCatalogMicroserviceApp {

	public static void main(String[] args) {
		SpringApplication.run(GeoCatalogMicroserviceApp.class, args);
	}
}
