package ch.tbz.flug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import ch.tbz.flug.airport.AirportClientProperties;

@SpringBootApplication
@EnableConfigurationProperties(AirportClientProperties.class)
public class FlugServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlugServiceApplication.class, args);
    }
}
