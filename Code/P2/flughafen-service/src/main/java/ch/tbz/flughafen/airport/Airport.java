package ch.tbz.flughafen.airport;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "airports")
public class Airport {

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String code;

    private int capacity;

    private Instant createdAt;

    protected Airport() {
    }

    public Airport(String id, String name, String code, int capacity, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.capacity = capacity;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
