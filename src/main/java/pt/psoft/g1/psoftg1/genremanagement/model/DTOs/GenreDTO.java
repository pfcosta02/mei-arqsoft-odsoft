package pt.psoft.g1.psoftg1.genremanagement.model.DTOs;

import java.io.Serializable;
import java.util.List;

public class GenreDTO implements Serializable {
    private String pk;
    private String genre;

    public GenreDTO() {}

    public GenreDTO(String pk, String genre) {
        this.pk = pk;
        this.genre = genre;
    }

    public String getPk() { return pk; }
    public String getGenre() { return genre; }
}