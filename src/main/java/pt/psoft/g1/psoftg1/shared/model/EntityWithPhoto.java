package pt.psoft.g1.psoftg1.shared.model;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public abstract class EntityWithPhoto
{
    protected Photo photo;

    // Setter
    protected void setPhotoInternal(Photo photoURI)
    {
        setPhotoInternal(photoURI.getPhotoFile());
    }

    protected void setPhotoInternal(String photoURI)
    {
        if (photoURI == null)
        {
            this.photo = null;
        }
        else
        {
            try
            {
                //If the Path object instantiation succeeds, it means that we have a valid Path
                this.photo = new Photo(Path.of(photoURI));
            }
            catch (InvalidPathException e)
            {
                //For some reason it failed, let's set to null to avoid invalid references to photos
                this.photo = null;
            }
        }
    }

    // Getter
    public Photo getPhoto()
    {
        return photo;
    }
}
