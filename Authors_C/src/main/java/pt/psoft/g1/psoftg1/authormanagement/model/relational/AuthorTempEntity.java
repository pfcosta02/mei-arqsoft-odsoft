package pt.psoft.g1.psoftg1.authormanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;

@Entity
@Table(name = "author_temp")
public class AuthorTempEntity {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String authorNumber;

    @Embedded
    private NameEntity name;

    @Embedded
    private BioEntity bio;

    // correlation id
    @NotNull
    private String isbn;

    private boolean bookCreated;

    protected AuthorTempEntity() {}

    public AuthorTempEntity(NameEntity name, BioEntity bio, String isbn) {
        this.name = name;
        this.bio = bio;
        this.isbn = isbn;
        this.bookCreated = false;
    }

    public String getAuthorNumber() {
        return authorNumber;
    }
    public void setAuthorNumber(String authorNumber) {this.authorNumber = authorNumber;}
}
