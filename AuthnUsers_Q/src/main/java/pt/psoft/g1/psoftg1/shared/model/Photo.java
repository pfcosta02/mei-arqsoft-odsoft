package pt.psoft.g1.psoftg1.shared.model;

import java.nio.file.Path;

public class Photo 
{
    // private long pk;
    private long id;
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
    // public void setId(String id) { this.id = id; }

    // Getter
    public String getPhotoFile() { return this.photoFile; }
    public long getId() { return id; }
}