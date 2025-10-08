package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

import java.io.Serializable;

@Profile("mongodb")
@Primary
public class DescriptionMongoDB implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long DescriptionId;

    @Size(max = Description.DESC_MAX_LENGTH)
    @Column(length = Description.DESC_MAX_LENGTH)
    private String description;

    public DescriptionMongoDB(String description) {
        setDescription(description);
    }

    protected DescriptionMongoDB() {}

    private void setDescription(String description)
    {
        this.description = description;
    }

    public String toString() {
        return this.description;
    }
}

