package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Profile("mongodb")
public class BioMongoDB {

    @Id
    @Getter @Setter
    private String bioId;

    private static final int BIO_MAX_LENGTH = 4096;

    @Field("bio")  // Optional: Map the field explicitly
    @NotNull
    @Size(min = 1, max = BIO_MAX_LENGTH)
    @Getter
    private String bio;

    public BioMongoDB(String bio) {
        setBio(bio);
    }

    protected BioMongoDB() {
        // For ORM or deserialization
    }

    // Setters
    private void setBio(String bio) { this.bio = bio; }
}