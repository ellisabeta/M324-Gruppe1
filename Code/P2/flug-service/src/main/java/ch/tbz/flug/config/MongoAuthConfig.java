package ch.tbz.flug.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.MongoCredential;

@Configuration
public class MongoAuthConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoAuthCustomizer(
            @Value("${spring.data.mongodb.username:${DB_USERNAME:${MONGODB_USERNAME:}}}") String username,
            @Value("${spring.data.mongodb.password:${DB_PASSWORD:${MONGODB_PASSWORD:}}}") String password,
            @Value("${spring.data.mongodb.authentication-database:${DB_AUTH_DB:${MONGODB_AUTH_DB:admin}}}") String authDb) {
        return builder -> {
            if (username != null && !username.trim().isEmpty() && password != null && !password.trim().isEmpty()) {
                builder.credential(MongoCredential.createCredential(username.trim(), authDb, password.trim().toCharArray()));
            }
        };
    }
}

