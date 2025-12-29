package pt.psoft.g1.psoftg1.bookmanagement.services;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookAuthorGenreDTO {
    private String isbn;
    private String title;
    private String description;
    private String genre;
    private List<String> authors;

}
