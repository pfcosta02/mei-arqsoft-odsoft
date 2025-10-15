package pt.psoft.g1.psoftg1.authormanagement.model;

import org.hibernate.StaleObjectStateException;
import pt.psoft.g1.psoftg1.authormanagement.services.UpdateAuthorRequest;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.Photo;

import java.util.Objects;

public class Author extends EntityWithPhoto
{
    private String authorNumber;
    private Long version;
    private Name name;
    private Bio bio;

    public Author(Name name, Bio bio, Photo photoURI)
    {
        setName(name);
        setBio(bio);
        setPhotoInternal(photoURI);
        this.version = 0L;
    }

    public Author(String name, String bio, String photoURI)
    {
        this(new Name(name), new Bio(bio), new Photo(photoURI));
    }

    protected Author() { }

    // Getters
    public String getAuthorNumber() { return authorNumber; }
    public Long getVersion() { return version; }
    public Name getName() { return name; }
    public Bio getBio() { return bio; }

    // Setters
    private void setName(Name name)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name cannot be null");
        }

        this.name = name;
    }

    private void setBio(Bio bio)
    {
        if (bio == null)
        {
            throw new IllegalArgumentException("Bio cannot be null");
        }

        this.bio = bio;
    }

    // Logica de negocio
    public void applyPatch(final Long expectedVersion, final UpdateAuthorRequest request)
    {
        if (!Objects.equals(this.version, expectedVersion))
        {
            throw new StaleObjectStateException("Object was already modified by another user", this.authorNumber);
        }

        if (request.getName() != null)
        {
            setName(new Name(request.getName()));
        }

        if (request.getBio() != null)
        {
            setBio(new Bio(request.getBio()));
        }

        if (request.getPhotoURI() != null)
        {
            setPhotoInternal(request.getPhotoURI());
        }
    }

    public void removePhoto(Long expectedVersion)
    {
        if (!Objects.equals(expectedVersion, this.version))
        {
            throw new ConflictException("Provided version does not match latest version of this object");
        }

        setPhotoInternal((String) null);
    }
}





