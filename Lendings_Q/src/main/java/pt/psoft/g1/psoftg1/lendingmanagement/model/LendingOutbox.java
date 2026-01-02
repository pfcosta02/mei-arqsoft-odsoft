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
    private Long aggregateId;  // id do Lending

    @Column(nullable = false)
    private String eventType;  // "lending.created", "lending.updated", etc

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;    // JSON do evento

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private Boolean published = false;

    public LendingOutbox(Long aggregateId, String eventType, String payload) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
        this.published = false;
    }
}
