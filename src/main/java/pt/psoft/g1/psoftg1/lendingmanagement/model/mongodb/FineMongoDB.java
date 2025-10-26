package pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "fines")
@Profile("mongodb")
public class FineMongoDB {

    @Id
    @Getter
    private String fineId;

    @Field("fineValuePerDayInCents")
    @PositiveOrZero
    @Getter
    private int fineValuePerDayInCents;

    @Field("centsValue")
    @PositiveOrZero
    @Getter
    private int centsValue;

    @Setter @Getter
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