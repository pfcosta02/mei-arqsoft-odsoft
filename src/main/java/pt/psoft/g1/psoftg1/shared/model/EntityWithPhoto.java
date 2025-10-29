package pt.psoft.g1.psoftg1.shared.model;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public abstract class EntityWithPhoto
{
    protected Photo photo;

    protected void setPhotoInternal(String photo)
    {
        if (photo == null)
        {
            setPhotoInternal((Photo) null);
            return;
        }

        try
        {
            setPhotoInternal(new Photo(Path.of(photo)));
        }
        catch (InvalidPathException e)
        {
            setPhotoInternal((Photo) null);
        }
    }

    protected void setPhotoInternal(Photo photo) {
        this.photo = photo; // already null-safe
    }

    public void setPhotoByClass(Photo photo)
    {
        setPhotoInternal(photo);
    }

    // Getter
    public Photo getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        setPhotoInternal(photo);
    }
}
