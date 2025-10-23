package pt.psoft.g1.psoftg1.bookmanagement.model;

public class Title {
    public static final int TITLE_MAX_LENGTH = 128;

    private String title;

    public Title(String title)
    {
        setTitle(title);
    }

    public void setTitle(String title)
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

    // Getter
    public String getTitle()
    {
        return this.title;
    }

    // Helpers
    @Override
    public String toString()
    {
        return this.title;
    }
}
