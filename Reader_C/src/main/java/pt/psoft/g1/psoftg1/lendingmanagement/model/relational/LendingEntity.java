package pt.psoft.g1.psoftg1.lendingmanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
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
    private String id;

    @Embedded
    @Getter
    private LendingNumberEntity lendingNumber;

    @NotNull
    @Column(name = "book_id", nullable = false)
    private String bookId;

    @NotNull
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

    protected LendingEntity() {}

    public LendingEntity(String bookId, ReaderDetailsEntity readerDetails, LendingNumberEntity lendingNumber,
                         LocalDate startDate, LocalDate limitDate, LocalDate returnedDate, int fineValuePerDayInCents,
                         String commentary) {
        this.bookId = bookId;
        this.readerDetails = readerDetails;
        this.lendingNumber = lendingNumber;
        this.startDate = startDate;
        this.limitDate = limitDate;
        this.returnedDate = returnedDate;
        this.fineValuePerDayInCents = fineValuePerDayInCents;
        this.commentary = commentary;
    }

    // Setter
    public void setId(String id) { this.id = id; }
    public void setBookId(String bookId) { this.bookId = bookId;}
    public void setReaderDetails(ReaderDetailsEntity readerDetails) { this.readerDetails = readerDetails;}

    // Getter
    public String getId() { return id; }
    public String getBookId() { return bookId; }
    public ReaderDetailsEntity getReaderDetails() { return readerDetails; }
}
