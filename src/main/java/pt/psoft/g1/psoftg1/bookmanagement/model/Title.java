package pt.psoft.g1.psoftg1.bookmanagement.model;

public class Title {
    public static final int TITLE_MAX_LENGTH = 128;

    private final String title;

    public Title(String title)
    {
        if (title == null)
        {
            throw new IllegalArgumentException("Title cannot be null");
        }
        if (title.isBlank())
        {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (title.length() > TITLE_MAX_LENGTH)
        {
            throw new IllegalArgumentException("Title has a maximum of " + TITLE_MAX_LENGTH + " characters");
        }

        this.title = title.strip();
    }

    protected Title() { this.title = null; }

    // Getter
    public String getTitle()
    {
        return this.title;
    }

    // Helpers
    public String toString()
    {
        return this.title;
    }
}
