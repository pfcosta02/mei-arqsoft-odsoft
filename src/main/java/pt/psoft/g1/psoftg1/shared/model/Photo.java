package pt.psoft.g1.psoftg1.shared.model;

import java.nio.file.Path;

public class Photo
{
    private long pk;
    private String photoFile;

    public Photo(Path photoPath)
    {
        setPhotoFile(photoPath.toString());
    }

    public Photo(String photoPath)
    {
        setPhotoFile(photoPath);
    }

    protected Photo()
    {
        // for frameworks if needed
    }

    // Setter
    private void setPhotoFile(String photofile)
    {
        if (photofile == null)
        {
            throw new IllegalArgumentException("PhotoFile cannot be null");
        }

        this.photoFile = photofile;
    }

    // Getter
    public String getPhotoFile() { return this.photoFile; }

    // Helper
    public String toString() { return this.photoFile; }
}


