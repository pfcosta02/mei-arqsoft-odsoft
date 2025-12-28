package pt.psoft.g1.psoftg1.shared.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Profile("jpa")
@Primary
@Getter
@MappedSuperclass
public abstract class EntityWithPhotoEntity 
{
    @Nullable
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="photo_id", nullable = true)
    @Setter
    @Getter
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
    protected void setPhotoInternal(String photo)
    {
        setPhotoInternal(new PhotoEntity(photo));
    }
}