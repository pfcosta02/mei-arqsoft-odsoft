package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;

import java.io.Serializable;

@Profile("mongodb")
@Document(collection = "descriptions")
public class DescriptionMongoDB implements Serializable {

    @Size(max = Description.DESC_MAX_LENGTH)
    @Field("description")
    private String description;

    public DescriptionMongoDB(String description) {
        setDescription(description);
    }

    protected DescriptionMongoDB() {}

    public String getDescription() { return description; }
    private void setDescription(String description) { this.description = description; }
}