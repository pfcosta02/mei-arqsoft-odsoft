package pt.psoft.g1.psoftg1.shared.model;

public class Name 
{
    public static final int NAME_MAX_LENGTH = 150;
    private String name;

    public Name(String name) 
    {
        setName(name);
    }

    protected Name() 
    {
        // for frameworks if needed
    }

    // Setter
    public void setName(String name) 
    {
        if (name == null) 
        {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (name.isBlank())
        { 
            throw new IllegalArgumentException("Name cannot be blank or only white spaces");
        }
        if (name.length() > NAME_MAX_LENGTH)
        {
            throw new IllegalArgumentException("Name cannot be more than " + NAME_MAX_LENGTH + " characters");
        }
        /* Alterei isto porque senao retorna excecao em espacos */
        if (!name.matches("[a-zA-Z ]+"))
        {
            throw new IllegalArgumentException("Name can only contain alphanumeric characters");
        }

        this.name = name;
    }

    // Getter
    public String getName() { return this.name; }

    // Helper
    @Override 
    public String toString(){ return this.name; }
}