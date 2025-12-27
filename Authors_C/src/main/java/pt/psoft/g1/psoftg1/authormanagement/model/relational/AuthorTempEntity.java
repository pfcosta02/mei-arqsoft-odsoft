package pt.psoft.g1.psoftg1.authormanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "author_temp")
public class AuthorTempEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @NotNull
    private String name;

    private String bio;

    // correlation id
    @NotNull
    private String isbn;

    private boolean bookCreated;

    protected AuthorTempEntity() {}

    public AuthorTempEntity(String name, String bio, String isbn) {
        this.name = name;
        this.bio = bio;
        this.isbn = isbn;
        this.bookCreated = false;
    }
}
