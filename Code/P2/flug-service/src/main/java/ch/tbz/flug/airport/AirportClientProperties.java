package ch.tbz.flug.airport;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.airport")
public record AirportClientProperties(String url) {
}
