package pt.psoft.g1.psoftg1.authormanagement.model.mongodb;

import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoDB;

@Profile("mongodb")
@Primary
@Document(collection = "authors")
// @EnableMongoAuditing
public class AuthorMongoDB extends EntityWithPhotoMongoDB {

    @Id
    @Getter
    private String authorNumber;

    @Version
    private long version;

    @Field("name")
    private NameMongoDB name;

    @Field("bio")
    private BioMongoDB bio;

    protected AuthorMongoDB() {}

    public AuthorMongoDB(NameMongoDB name, BioMongoDB bio, PhotoMongoDB photo)
    {
        setName(name);
        setBio(bio);
        setPhoto(photo);
    }

    // Getters
    public Long getVersion() { return version; }
    public NameMongoDB getName() { return name; }
    public BioMongoDB getBio() { return bio; }

    // Setters
    public void setName(NameMongoDB name) { this.name = name; }
    public void setBio(BioMongoDB bio) { this.bio = bio; }
}