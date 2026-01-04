package pt.psoft.g1.psoftg1.bookmanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;


import pt.psoft.g1.psoftg1.shared.model.relational.EntityWithPhotoEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("jpa")
@Primary
@Entity
@Table(name = "Book", uniqueConstraints = {
        @UniqueConstraint(name = "uc_book_isbn", columnNames = {"ISBN"})
})
public class BookEntity extends EntityWithPhotoEntity
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long pk;

    @Version
    @Getter
    private Long version;

    @Embedded
    private IsbnEntity isbn;


    public BookEntity(IsbnEntity isbn)
    {
        setIsbn(isbn);

        this.version = 0L;
    }

    protected BookEntity() {}

    // Setters
    public void setIsbn(IsbnEntity isbn) { this.isbn = isbn; }

    // Getters
    public String getIsbn(){ return this.isbn.toString(); }
    public Long getPk() { return pk; }
}
