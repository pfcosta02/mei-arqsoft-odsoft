package pt.psoft.g1.psoftg1.genremanagement.model.redis;

public class GenreRedisDTO {
    private String pk;
    private String genre;

    public GenreRedisDTO() {}

    public GenreRedisDTO(String pk, String genre) {
        this.pk = pk;
        this.genre = genre;
    }


    // Getters e setters
    public String getPk() {
        return pk;
    }
    public void setPk(String pk) {
        this.pk = pk;
    }
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
}
