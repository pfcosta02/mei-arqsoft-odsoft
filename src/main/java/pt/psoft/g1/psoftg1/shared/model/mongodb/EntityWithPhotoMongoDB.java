package pt.psoft.g1.psoftg1.shared.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Profile("mongodb")
public abstract class EntityWithPhotoMongoDB {

    @Field("photo")  // Embedding the photo, or use `@DBRef` if referencing another document
    @Getter @Setter
    private PhotoMongoDB photo;

    protected EntityWithPhotoMongoDB() { }

    public EntityWithPhotoMongoDB(PhotoMongoDB photo) { setPhotoInternal(photo); }

    protected void setPhotoInternal(PhotoMongoDB photoURI) { this.photo = photoURI; }
    protected void setPhotoInternal(String photo) { setPhotoInternal(new PhotoMongoDB(photo)); }
}