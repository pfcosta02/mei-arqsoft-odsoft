package pt.psoft.g1.psoftg1.lendingmanagement.model.elasticSearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch.BookES;
import pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch.ReaderDetailsES;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Elasticsearch version of Lending entity.
 */
@Getter
@Setter
@Document(indexName = "lendings")
public class LendingES {

    @Id
    private String id;

    @Field(type = FieldType.Object)
    private LendingNumberES lendingNumber;

    @Field(type = FieldType.Object)
    private BookES book;

    @Field(type = FieldType.Object)
    private ReaderDetailsES readerDetails;

    @Field(type = FieldType.Date)
    private LocalDate startDate;

    @Field(type = FieldType.Date)
    private LocalDate limitDate;

    @Field(type = FieldType.Date)
    private LocalDate returnedDate;

    @Field(type = FieldType.Integer)
    private int fineValuePerDayInCents;

    @Field(type = FieldType.Text)
    private String commentary;

    public LendingES(BookES book, ReaderDetailsES readerDetails, int seq, int lendingDuration, int fineValuePerDayInCents) {
        this.book = book;
        this.readerDetails = readerDetails;
        this.lendingNumber = new LendingNumberES(seq);
        this.startDate = LocalDate.now();
        this.limitDate = LocalDate.now().plusDays(lendingDuration);
        this.returnedDate = null;
        this.fineValuePerDayInCents = fineValuePerDayInCents;
    }

    public LendingES() {}

    public int getDaysDelayed() {
        if (returnedDate != null) {
            return Math.max((int) java.time.temporal.ChronoUnit.DAYS.between(limitDate, returnedDate), 0);
        } else {
            return Math.max((int) java.time.temporal.ChronoUnit.DAYS.between(limitDate, LocalDate.now()), 0);
        }
    }

    public Optional<Integer> getFineValueInCents() {
        int days = getDaysDelayed();
        if (days > 0) {
            return Optional.of(fineValuePerDayInCents * days);
        }
        return Optional.empty();
    }

    public String getLendingNumber() {
        return lendingNumber != null ? lendingNumber.toString() : null;
    }
}