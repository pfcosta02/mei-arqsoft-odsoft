package pt.psoft.g1.psoftg1.lendingmanagement.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * The {@code LendingNumber} class handles the business logic of the identifier of a {@code Lending}.
 * <p>
 * It stores the year of the lending and a sequential number, and a string combining these two.
 */
public class LendingNumber implements Serializable
{
    private final String lendingNumber;

    /**
     * Constructs a new {@code LendingNumber} object based on a year and a given sequential number.
     */
    public LendingNumber(int year, int sequential)
    {
        if (year < 1970 || LocalDate.now().getYear() < year)
        {
            throw new IllegalArgumentException("Invalid year component");
        }
        if (sequential < 0)
        {
            throw new IllegalArgumentException("Sequential component cannot be negative");
        }

        this.lendingNumber = year + "/" + sequential;
    }

    /**
     * Constructs a new {@code LendingNumber} object based on a string.
     * <p>
     * Initialization may fail if the format is not as expected.
     */
    public LendingNumber(String lendingNumber)
    {
        if (lendingNumber == null)
        {
            throw new IllegalArgumentException("Lending number cannot be null");
        }

        int year, sequential;
        try
        {
            year = Integer.parseInt(lendingNumber, 0, 4, 10);
            sequential = Integer.parseInt(lendingNumber, 5, lendingNumber.length(), 10);
            if (lendingNumber.charAt(4) != '/')
            {
                throw new IllegalArgumentException("Lending number has wrong format. It should be \"{year}/{sequential}\"");
            }
        }
        catch (NumberFormatException | IndexOutOfBoundsException e)
        {
            throw new IllegalArgumentException("Lending number has wrong format. It should be \"{year}/{sequential}\"");
        }
        this.lendingNumber = year + "/" + sequential;
    }

    /**
     * Constructs a new {@code LendingNumber} object based on a sequential number.
     * Year is automatically set to current year.
     */
    public LendingNumber(int sequential)
    {
        if (sequential < 0)
        {
            throw new IllegalArgumentException("Sequential component cannot be negative");
        }

        this.lendingNumber = LocalDate.now().getYear() + "/" + sequential;
    }

    public String getLendingNumber() { return lendingNumber; }

    // Helper
    @Override
    public String toString()
    {
        return this.lendingNumber;
    }
}