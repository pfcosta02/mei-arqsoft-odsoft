package pt.psoft.g1.psoftg1.shared.model.relational;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.shared.model.OutboxEnum;

@Entity
@Table(name = "outbox_event")
@Getter
@Setter
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateId; // ID do Reader ou outro agregado
    private String eventType;   // Tipo do evento (BOOK_CREATED, etc.)

    @Lob
    private String payload;     // JSON do objeto

    @Enumerated(EnumType.STRING)
    private OutboxEnum status = OutboxEnum.NEW; // Default: NEW
}
