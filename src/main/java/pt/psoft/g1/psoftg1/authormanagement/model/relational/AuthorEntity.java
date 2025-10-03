package pt.psoft.g1.psoftg1.authormanagement.model.relational;

import jakarta.persistence.*;
import lombok.Getter;
import pt.psoft.g1.psoftg1.shared.model.relational.EntityWithPhotoEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("jpa")
@Primary
@Entity
@Table(name = "AUTHOR")
public class AuthorEntity extends EntityWithPhotoEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "AUTHOR_NUMBER")
    @Getter
    private Long authorNumber;

    @Version
    private Long version;

    @Embedded
    private NameEntity name;

    @Embedded
    private BioEntity bio;

    protected AuthorEntity() {}

    public AuthorEntity(NameEntity name, BioEntity bio, PhotoEntity photoURI)
    {
        setName(name);
        setBio(bio);
        setPhoto(photoURI);
    }

    // Getters
    public Long getVersion() { return version; }
    public NameEntity getName() { return name; }
    public BioEntity getBio() { return bio; }

    // Setters
    private void setName(NameEntity name) { this.name = name; }
    private void setBio(BioEntity bio) { this.bio = bio; }
}
