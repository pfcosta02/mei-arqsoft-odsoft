package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

@Profile("mongodb")
@Primary
public class BioMongoDB {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private Long BioId;

    @Column(nullable = false, length = Bio.BIO_MAX_LENGTH)
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
    private void setBio(String bio) { this.bio = bio; }
}
