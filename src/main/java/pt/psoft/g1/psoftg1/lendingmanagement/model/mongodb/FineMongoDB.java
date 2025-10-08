package pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "fines")
@Profile("mongodb")
@Primary
public class FineMongoDB {
    @Id
    @Getter
    private Long pk;

    @Field("fineValuePerDayInCents")
    @PositiveOrZero
    @Column(updatable = false)
    @Getter
    private int fineValuePerDayInCents;

    @Field("centsValue")
    @PositiveOrZero
    @Getter
    private int centsValue;

    @Setter

    @OneToOne(optional = false, orphanRemoval = true)
    @JoinColumn(name = "lending_pk", nullable = false, unique = true)
    @Field("lending")
    private LendingMongoDB lendingMongoDB;

    protected FineMongoDB() { }

    public FineMongoDB(int fineValuePerDayInCents, int centsValue, LendingMongoDB lending)
    {
        this.fineValuePerDayInCents = fineValuePerDayInCents;
        this.centsValue = centsValue;
        this.lendingMongoDB = lending;
    }
}