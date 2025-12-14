package pt.psoft.g1.psoftg1.authormanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("jpa")
@Primary
//@Entity
@Embeddable
public class BioEntity
{
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Getter @Setter
//    private Long BioId;

    @Column(nullable = false, length = Bio.BIO_MAX_LENGTH)
    @NotNull
    @Size(min = 1, max = Bio.BIO_MAX_LENGTH)
    @Getter
    private String bio;

    protected BioEntity() { }

    public BioEntity(String bio)
    {
        setBio(bio);
    }

    // Setters
    private void setBio(String bio) { this.bio = bio; }
}
