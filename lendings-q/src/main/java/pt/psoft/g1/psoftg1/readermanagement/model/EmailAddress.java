package pt.psoft.g1.psoftg1.readermanagement.model;

public class EmailAddress {

    private final String address;

    public EmailAddress(String address)
    {
        if (address == null || address.isBlank())
        {
            throw new IllegalArgumentException("Email não pode ser nulo ou vazio");
        }
        this.address = address;
    }

    // Getter
    public String getAddress()
    {
        return address;
    }

    // Helper
    @Override
    public String toString()
    {
        return address;
    }
}
