package pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoDB;

import java.time.LocalDate;

@Document(collection = "lendings")
@Profile("mongodb")
@Primary
public class LendingMongoDB {

    @Id
    private String lendingId;

    @Field("lending_number")
    @Getter
    private LendingNumberMongoDB lendingNumber;

    @Field("book")
    @NotNull
    @Getter
    private BookMongoDB book;

    @Field("reader_details")
    @NotNull
    @Getter
    private ReaderDetailsMongoDB readerDetails;

    @Field("start_date")
    @NotNull
    @Getter
    private LocalDate startDate;

    @Field("limit_date")
    @NotNull
    @Getter
    private LocalDate limitDate;

    @Field("returned_date")
    @Getter
    private LocalDate returnedDate;

    @Field("version")
    @Version
    @Getter
    private long version;

    @Field("commentary")
    @Size(min = 0, max = 1024)
    @Getter
    private String commentary;

    @Field("fineValuePerDayInCents")
    @Getter
    private int fineValuePerDayInCents;

    @Field("days_untul_return")
    @Getter @Setter
    private Integer daysUntilReturn;

    @Field("days_overdue")
    @Getter @Setter
    private Integer daysOverdue;

    protected LendingMongoDB() {}

    public LendingMongoDB(BookMongoDB book, ReaderDetailsMongoDB readerDetails, LendingNumberMongoDB lendingNumber,
                         LocalDate startDate, LocalDate limitDate, LocalDate returnedDate, int fineValuePerDayInCents,
                         String commentary) {
        this.book = book;
        this.readerDetails = readerDetails;
        this.lendingNumber = lendingNumber;
        this.startDate = startDate;
        this.limitDate = limitDate;
        this.returnedDate = returnedDate;
        this.fineValuePerDayInCents = fineValuePerDayInCents;
        this.commentary = commentary;
    }

    public String getLendingId() { return lendingId; }
    public void setLendingId(String lendingId) { this.lendingId = lendingId; }
}