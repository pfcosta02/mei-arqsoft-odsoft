package pt.psoft.g1.psoftg1.readermanagement.model;

import java.io.Serializable;
import java.time.LocalDate;


public class ReaderNumber implements Serializable
{
    private final String readerNumber;

    public ReaderNumber(int year, int number)
    {
        this.readerNumber = year + "/" + number;
    }

    public ReaderNumber(int number)
    {
        this.readerNumber = LocalDate.now().getYear() + "/" + number;
    }

    // Getter
    public String getReaderNumber()
    {
        return readerNumber;
    }

    // Helper
    public String toString() {
        return readerNumber;
    }
}


