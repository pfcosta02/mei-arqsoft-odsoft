package pt.psoft.g1.psoftg1.lendingmanagement.model;

import java.util.Objects;

public class Fine
{
    private String id;
    private final int fineValuePerDayInCents;
    private final int centsValue;

    private final Lending lending;

    public Fine(Lending lending)
    {
        if (lending == null)
        {
            throw new IllegalArgumentException("Lending cannot be null");
        }

        if (lending.getDaysDelayed() <= 0)
        {
            throw new IllegalArgumentException("Lending is not overdue");
        }

        this.fineValuePerDayInCents = lending.getFineValuePerDayInCents();
        this.centsValue = fineValuePerDayInCents * lending.getDaysDelayed();
        this.lending = lending;
    }

    protected Fine(int fineValuePerDayInCents, int centsValue, Lending lending)
    {
        this.fineValuePerDayInCents = fineValuePerDayInCents;
        this.centsValue = centsValue;
        this.lending = Objects.requireNonNull(lending);
    }

    // Getters
    public int getFineValuePerDayInCents() { return fineValuePerDayInCents; }
    public int getCentsValue() { return centsValue; }
    public Lending getLending() { return lending; }
    public String getId() { return id; }

    // Setters
    public void setId(String id) {this.id = id;}
}
