package pt.psoft.g1.psoftg1.shared.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Profile("jpa")
@Primary
public class EntityWithPhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "photo_id")
    @Getter
    @Setter
    private PhotoEntity photo;

    protected EntityWithPhotoEntity() { }

    public EntityWithPhotoEntity(PhotoEntity photo)
    {
        setPhotoInternal(photo);
    }

    protected void setPhotoInternal(PhotoEntity photoURI)
    {
        this.photo = photoURI;
    }
    protected void setPhotoInternal(String photoURI)
    {
        setPhotoInternal(new PhotoEntity(photoURI));
    }
}
