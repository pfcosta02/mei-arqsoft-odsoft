package pt.psoft.g1.psoftg1.readermanagement.model;

import java.io.Serializable;
import java.time.LocalDate;

public class ReaderNumber implements Serializable
{
    private String readerNumber;

    public ReaderNumber(int year, int number)
    {
        this.readerNumber = year + "/" + number;
    }

    public ReaderNumber(int number)
    {
        this.readerNumber = LocalDate.now().getYear() + "/" + number;
    }

    public ReaderNumber(String readerNumber)
    {
        if (readerNumber == null || !readerNumber.matches("\\d{4}/\\d+"))
        {
            throw new IllegalArgumentException(
                    "Invalid reader number. Expected format: YYYY/number, got: " + readerNumber
            );
        }
        this.readerNumber = readerNumber;
    }

    protected ReaderNumber() {
        // for frameworks (Jackson, JPA, etc.)
    }

    // Getter
    public String getReaderNumber()
    {
        return readerNumber;
    }

    // Helper
    @Override
    public String toString() {
        return readerNumber;
    }
}


