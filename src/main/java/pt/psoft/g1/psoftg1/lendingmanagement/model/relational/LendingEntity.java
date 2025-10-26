package pt.psoft.g1.psoftg1.lendingmanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import java.time.LocalDate;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Table(name="Lending", uniqueConstraints = {
        @UniqueConstraint(columnNames={"LENDING_NUMBER"})})
@Profile("jpa")
@Primary
public class LendingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String lendingId;

    @Embedded
    @Getter
    private LendingNumberEntity lendingNumber;

    @NotNull
    @Getter
    @ManyToOne(fetch=FetchType.EAGER, optional = false)
    private BookEntity book;

    @NotNull
    @Getter
    @ManyToOne(fetch=FetchType.EAGER, optional = false)
    private ReaderDetailsEntity readerDetails;

    @NotNull
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    @Getter
    private LocalDate startDate;

    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @Getter
    private LocalDate limitDate;

    @Temporal(TemporalType.DATE)
    @Getter
    private LocalDate returnedDate;

    @Version
    @Getter
    private long version;

    @Size(min = 0, max = 1024)
    @Column(length = 1024)
    @Getter
    private String commentary;

    @Getter
    private int fineValuePerDayInCents;

    @Transient
    private Integer daysUntilReturn;

    @Transient
    private Integer daysOverdue;

    protected LendingEntity() {}

    public LendingEntity(BookEntity book, ReaderDetailsEntity readerDetails, LendingNumberEntity lendingNumber,
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

    // Setter
    public void setBook(BookEntity book) { this.book = book;}
    public void setReaderDetails(ReaderDetailsEntity readerDetails) { this.readerDetails = readerDetails;}

    // Getter
    public BookEntity getBook() { return book; }
    public ReaderDetailsEntity getReaderDetails() { return readerDetails; }
}