package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document
@Profile("mongodb")
@Primary
public class EntityWithPhotoMongoDB {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Field("photo")  // Embedding the photo, or use `@DBRef` if referencing another document
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id")
    @Getter
    @Setter
    private PhotoMongoDB photo;

    protected EntityWithPhotoMongoDB() { }

    public EntityWithPhotoMongoDB(PhotoMongoDB photo)
    {
        setPhotoInternal(photo);
    }

    protected void setPhotoInternal(PhotoMongoDB photoURI)
    {
        this.photo = photoURI;
    }
    protected void setPhotoInternal(String photoURI)
    {
        setPhotoInternal(new PhotoMongoDB(photoURI));
    }
}
