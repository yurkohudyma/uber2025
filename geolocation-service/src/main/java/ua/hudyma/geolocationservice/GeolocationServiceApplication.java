package ua.hudyma.geolocationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GeolocationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GeolocationServiceApplication.class, args);
    }

}
