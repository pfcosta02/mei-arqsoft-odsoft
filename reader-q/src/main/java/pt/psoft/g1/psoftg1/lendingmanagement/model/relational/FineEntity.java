package pt.psoft.g1.psoftg1.lendingmanagement.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Fine")
@Profile("jpa")
@Primary
public class FineEntity {
    @Id
    @Getter
    @Setter
    private String id;

    @PositiveOrZero
    @Column(updatable = false)
    @Getter
    private int fineValuePerDayInCents;

    @PositiveOrZero
    @Getter
    private int centsValue;

    @Setter
    @Getter
    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "lending_pk", nullable = false, unique = true)
    private LendingEntity lending;

    protected FineEntity() { }

    public FineEntity(int fineValuePerDayInCents, int centsValue, LendingEntity lending)
    {
        this.fineValuePerDayInCents = fineValuePerDayInCents;
        this.centsValue = centsValue;
        this.lending = lending;
    }
}
