package ua.ikulikov.flightmaster.skyscannerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SkyScannerServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(SkyScannerServiceApp.class, args);
	}

}
