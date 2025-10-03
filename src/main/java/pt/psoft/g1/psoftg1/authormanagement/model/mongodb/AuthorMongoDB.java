package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.StaleObjectStateException;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.authormanagement.services.UpdateAuthorRequest;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;
import pt.psoft.g1.psoftg1.shared.services.generator.IdGeneratorFactory;

@Document(collection = "authors")
@EnableMongoAuditing// Optional: specify the collection name
public class AuthorMongoDB extends EntityWithPhotoMongoDB {

    @Id
    @Getter
    @Setter
    private String authorNumber;

    @Field("genId")
    @Getter
    @Setter
    private String genId;

//    @Field("authorNumber")
//    private Long authorNumber;

    @Version
    @Getter
    private Long version;  // MongoDB versioning (optional, manual control)

    @Field("name")
    private NameMongoDB name;

    @Field("bio")
    private BioMongoDB bio;


    // Constructor, getters, setters
    public AuthorMongoDB(String name, String bio, String photoURI, String genId) {
        setName(name);
        setBio(bio);
        setPhotoInternal(photoURI);
        setGenId(genId);
    }

    protected AuthorMongoDB() {
        // for ORM or deserialization only
    }

//    public void setAuthorNumber(Long authorNumber) {
//        this.authorNumber = authorNumber;
//    }

    public void setName(String name) {
        this.name = new NameMongoDB(name);
    }

    public void setBio(String bio) {
        this.bio = new BioMongoDB(bio);
    }

    public void applyPatch(final long desiredVersion, final UpdateAuthorRequest request) {
        if (!this.version.equals(desiredVersion)) {
            throw new StaleObjectStateException("Object was already modified by another user", this.authorNumber);
        }
        if (request.getName() != null) {
            setName(request.getName());
        }
        if (request.getBio() != null) {
            setBio(request.getBio());
        }
        if (request.getPhotoURI() != null) {
            setPhotoInternal(request.getPhotoURI());
        }
    }

    public void removePhoto(long desiredVersion) {
        if (desiredVersion != this.version) {
            throw new ConflictException("Provided version does not match latest version of this object");
        }
        setPhotoInternal(null);
    }

    public String getName() {
        return this.name.toString();
    }

    public String getBio() {
        return this.bio.toString();
    }

//    public Long getAuthorNumber() {
//        return authorNumber;
//    }

    public String getPhotoURI() {
        if (this.getPhoto() == null || this.getPhoto().getPhotoFile() == null) {
            return null; // Or return a default value, handle accordingly
        }
        return this.getPhoto().getPhotoFile().toString();
    }
}
