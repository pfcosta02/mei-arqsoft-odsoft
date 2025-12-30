package pt.psoft.g1.psoftg1.bookmanagement.model.relational;

import java.io.Serializable;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.validation.constraints.Size;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;

@Profile("jpa")
@Primary
@Embeddable
public class DescriptionEntity implements Serializable
{
    @Size(max = Description.DESC_MAX_LENGTH)
    @Column(length = Description.DESC_MAX_LENGTH)
    private String description;

    public DescriptionEntity(String description)
    {
        setDescription(description);
    }

    protected DescriptionEntity() {}

    // Getters
    public String getDescription()
    {
        return description;
    }

    // Setters
    private void setDescription(String description)
    {
        this.description = description;
    }
}

