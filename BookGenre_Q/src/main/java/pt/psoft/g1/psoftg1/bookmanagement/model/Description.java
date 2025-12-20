package pt.psoft.g1.psoftg1.bookmanagement.model;

import jakarta.annotation.Nullable;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

public class Description {
    public static final int DESC_MAX_LENGTH = 4096;
    private String description;

    public Description(String description)
    {
        setDescription(description);
    }

    protected Description() {}

    public void setDescription(@Nullable String description)
    {
        if (description == null || description.isBlank())
        {
            this.description = null;
        }
        else if (description.length() > DESC_MAX_LENGTH)
        {
            throw new IllegalArgumentException("Description has a maximum of " + DESC_MAX_LENGTH + " characters");
        }
        else
        {
            this.description = StringUtilsCustom.sanitizeHtml(description);
        }
    }

    // Getters
    public String getDescription()
    {
        return this.description;
    }

    // Helpers
    @Override
    public String toString()
    {
        return this.description;
    }
}
