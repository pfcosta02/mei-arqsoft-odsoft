package pt.psoft.g1.psoftg1.bookmanagement.model.relational;

import java.io.Serializable;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

@Profile("jpa")
@Primary
@Embeddable
@EqualsAndHashCode
public class IsbnEntity implements Serializable {

    @NotNull
    @Size(min = 10, max = 13)
    @Column(name = "ISBN", length = 16, unique = true, nullable = false)
    private String isbn;

    public IsbnEntity(String isbn)
    {
        setIsbn(isbn);
    }

    protected IsbnEntity() {}

    // Getters
    public String getIsbn()
    {
        return isbn;
    }

    // Setters
    private void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }
}