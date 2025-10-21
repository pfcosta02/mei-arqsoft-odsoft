package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;

@Document(collection = "bios")
@Profile("mongodb")
public class BioMongoDB {

    @Field("bio")  // Optional: Map the field explicitly
    @NotNull
    @Size(min = 1, max = Bio.BIO_MAX_LENGTH)
    @Getter
    private String bio;

    public BioMongoDB(String bio) {
        setBio(bio);
    }

    protected BioMongoDB() {
        // For ORM or deserialization
    }

    // Setters
    public void setBio(String bio) { this.bio = bio; }
}