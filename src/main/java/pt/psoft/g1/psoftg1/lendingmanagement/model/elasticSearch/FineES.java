package pt.psoft.g1.psoftg1.lendingmanagement.model.elasticSearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.validation.constraints.PositiveOrZero;
import java.util.Objects;

/**
 * The {@code FineES} class models a fine applied when a lending is past its due date.
 * Stored as a document in Elasticsearch.
 * @author rmfranca
 */
@Getter
@Setter
@Document(indexName = "fines")
public class FineES {

    @Id
    private String id; // Elasticsearch prefers String IDs

    @PositiveOrZero
    @Field(type = FieldType.Integer)
    private int fineValuePerDayInCents;

    /** Fine value in Euro cents */
    @PositiveOrZero
    @Field(type = FieldType.Integer)
    private int centsValue;

    @Field(type = FieldType.Object)
    private LendingES lending;

    /**
     * Constructs a new {@code FineES} object. Sets the current value of the fine,
     * as well as the fine value per day at the time of creation.
     * @param lending transaction which generates this fine.
     */
    public FineES(LendingES lending) {
        if (lending.getDaysDelayed() <= 0)
            throw new IllegalArgumentException("Lending is not overdue");
        fineValuePerDayInCents = lending.getFineValuePerDayInCents();
        centsValue = fineValuePerDayInCents * lending.getDaysDelayed();
        this.lending = Objects.requireNonNull(lending);
    }

    /** Protected empty constructor for framework use. */
    protected FineES() {}
}
