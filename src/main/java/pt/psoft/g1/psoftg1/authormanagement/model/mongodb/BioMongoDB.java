package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

public class BioMongoDB {

    private static final int BIO_MAX_LENGTH = 4096;

    @Field("bio")  // Optional: Map the field explicitly
    @NotNull
    @Size(min = 1, max = BIO_MAX_LENGTH)
    private String bio;

    public BioMongoDB(String bio) {
        setBio(bio);
    }

    protected BioMongoDB() {
        // For ORM or deserialization
    }

    public void setBio(String bio) {
        if (bio == null)
            throw new IllegalArgumentException("Bio cannot be null");
        if (bio.isBlank())
            throw new IllegalArgumentException("Bio cannot be blank");
        if (bio.length() > BIO_MAX_LENGTH)
            throw new IllegalArgumentException("Bio has a maximum of 4096 characters");
        this.bio = StringUtilsCustom.sanitizeHtml(bio);
    }

    @Override
    public String toString() {
        return bio;
    }
}
