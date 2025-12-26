package pt.psoft.g1.psoftg1.lendingmanagement.model;

import lombok.Builder;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import org.hibernate.StaleObjectStateException;

public class Lending
{
    // TODO: Substituir por ID e nao é suposto ser public
    public Long pk;
    private LendingNumber lendingNumber;
    private String bookId;
    private ReaderDetails readerDetails;
    private LocalDate startDate;
    private LocalDate limitDate;
    private LocalDate returnedDate;
    private long version;
    private String commentary;
    private Integer daysUntilReturn;
    private Integer daysOverdue;
    private int fineValuePerDayInCents;

    public Lending(String bookId, ReaderDetails readerDetails, int seq, int lendingDuration, int fineValuePerDayInCents)
    {
        try
        {
            this.bookId = Objects.requireNonNull(bookId);
            this.readerDetails = Objects.requireNonNull(readerDetails);
        }
        catch (NullPointerException e)
        {
            throw new IllegalArgumentException("Null objects passed to lending");
        }

        this.lendingNumber = new LendingNumber(seq);
        this.startDate = LocalDate.now();
        this.limitDate = LocalDate.now().plusDays(lendingDuration);
        this.returnedDate = null;
        this.fineValuePerDayInCents = fineValuePerDayInCents;

        setDaysUntilReturn();
        setDaysOverdue();
    }

    @Builder
    public Lending(String bookId, ReaderDetails readerDetails, LendingNumber lendingNumber, LocalDate startDate, LocalDate limitDate, LocalDate returnedDate, int fineValuePerDayInCents) {
        try
        {
            this.bookId = Objects.requireNonNull(bookId);
            this.readerDetails = Objects.requireNonNull(readerDetails);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Null objects passed to lending");
        }
        this.lendingNumber = lendingNumber;
        this.startDate = startDate;
        this.limitDate = limitDate;
        this.returnedDate = returnedDate;
        this.fineValuePerDayInCents = fineValuePerDayInCents;

        setDaysUntilReturn();
        setDaysOverdue();
    }

    // Getters
    public String getBook() { return bookId; }
    public ReaderDetails getReaderDetails() { return readerDetails; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getLimitDate() { return limitDate; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public String getCommentary() { return commentary; }
    public String getLendingNumber() { return lendingNumber.toString(); }
    public long getVersion() { return version; }

    /**
     * <p>Returns the number of days that the lending is/was past its due date</p>
     * @return      If the book was returned on time, or there is still time for it be returned, returns 0.
     * If the book has been returned with delay, returns the number of days of delay.
     * If the book has not been returned, returns the number of days
     * past its limit date.
     */
    public int getDaysDelayed()
    {
        if(returnedDate != null)
        {
            return Math.max((int) ChronoUnit.DAYS.between(limitDate, returnedDate), 0);
        }
        else
        {
            return Math.max((int) ChronoUnit.DAYS.between(limitDate, LocalDate.now()), 0);

        }
    }
    public Optional<Integer> getFineValueInCents()
    {
        int days = getDaysDelayed();
        return days > 0 ? Optional.of(fineValuePerDayInCents * days) : Optional.empty();
    }
    public int getFineValuePerDayInCents() { return fineValuePerDayInCents; }
    public Optional<Integer> getDaysUntilReturn()
    {
        setDaysUntilReturn();
        return Optional.ofNullable(daysUntilReturn);
    }

    public Optional<Integer> getDaysOverdue()
    {
        setDaysOverdue();
        return Optional.ofNullable(daysOverdue);
    }


    // Setters
    /**
     * <p>Sets {@code commentary} and the current date as {@code returnedDate}.
     * <p>If {@code returnedDate} is after {@code limitDate}, fine is applied with corresponding value.
     *
     * @param       desiredVersion to prevent editing a stale object.
     * @param       commentary written by a reader.
     * @throws      StaleObjectStateException if object was already modified by another user.
     * @throws      IllegalArgumentException  if {@code returnedDate} already has a value.
     */
    public void setReturned(final long desiredVersion, final String commentary){

        if (this.returnedDate != null)
        {
            throw new IllegalArgumentException("book has already been returned!");
        }

        // check current version
        if (this.version != desiredVersion)
        {
            throw new StaleObjectStateException("Object was already modified by another user", this.pk);
        }

        if(commentary != null)
        {
            this.commentary = commentary;
        }

        this.returnedDate = LocalDate.now();
    }

    private void setDaysUntilReturn(){
        int daysUntilReturn = (int) ChronoUnit.DAYS.between(LocalDate.now(), this.limitDate);
        if(this.returnedDate != null || daysUntilReturn < 0){
            this.daysUntilReturn = null;
        }else{
            this.daysUntilReturn = daysUntilReturn;
        }
    }

    private void setDaysOverdue(){
        int days = getDaysDelayed();
        if(days > 0){
            this.daysOverdue = days;
        }else{
            this.daysOverdue = null;
        }
    }

    protected Lending() {}

    //TODO: Provavelmente tenho de remover isto
    /**Factory method meant to be only used in bootstrapping.*/
    public static Lending newBootstrappingLending(String bookId,
                                                  ReaderDetails readerDetails,
                                                  int year,
                                                  int seq,
                                                  LocalDate startDate,
                                                  LocalDate returnedDate,
                                                  int lendingDuration,
                                                  int fineValuePerDayInCents)
    {
        Lending lending = new Lending(bookId, readerDetails, new LendingNumber(year, seq), startDate, startDate.plusDays(lendingDuration), returnedDate, fineValuePerDayInCents);

        return lending;
    }
}
