package pt.psoft.g1.psoftg1.lendingmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "lending_outbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LendingOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String aggregateId;  // lendingNumber (ex: "2024/1")

    @Column(nullable = false)
    private String eventType;    // "lending.created", etc

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;      // JSON

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private Boolean published = false;

    public LendingOutbox(String aggregateId, String eventType, String payload) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
        this.published = false;
    }
}
