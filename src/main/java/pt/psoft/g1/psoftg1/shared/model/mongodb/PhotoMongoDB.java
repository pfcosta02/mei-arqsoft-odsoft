package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Getter;
import lombok.Setter;


@Document(collection = "photos")
@Profile("mongodb")
@Primary
public class PhotoMongoDB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pk;

    @Field("photoFile")
    @NotNull
    @Setter
    @Getter
    private String photoFile;

    protected PhotoMongoDB() { }

    public PhotoMongoDB(String photoFile)
    {
        setPhotoFile(photoFile);
    }
}


