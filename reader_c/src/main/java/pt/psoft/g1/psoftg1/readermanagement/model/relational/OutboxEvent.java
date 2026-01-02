package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import pt.psoft.g1.psoftg1.readermanagement.model.OutboxEnum;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId; // ID do Reader ou outro agregado
    private String eventType;   // Tipo do evento (READER_CREATED, etc.)

    @Lob
    private String payload;     // JSON do objeto

    @Enumerated(EnumType.STRING)
    private OutboxEnum status = OutboxEnum.NEW; // Default: NEW

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime sentAt;
    private int retryCount = 0;
    private LocalDateTime escalatedAt;
}