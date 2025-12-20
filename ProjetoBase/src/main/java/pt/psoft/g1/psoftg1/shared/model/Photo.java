package pt.psoft.g1.psoftg1.shared.model;

import java.nio.file.Path;

public class Photo
{
    private long pk;
    private String photoFile;

    public Photo(Path photoFile)
    {
        setPhotoFile(photoFile.toString());
    }

    protected Photo()
    {
        // for frameworks if needed
    }

    // Setter
    private void setPhotoFile(String photofile)
    {
        this.photoFile = photofile;
    }

    // Getter
    public String getPhotoFile() { return this.photoFile; }
}


