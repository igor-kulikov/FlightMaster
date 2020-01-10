package ua.ikulikov.flightmaster.geocatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class GeoCatalogServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(GeoCatalogServiceApp.class, args);
	}
}
