package pt.psoft.g1.psoftg1.bookmanagement.model.DTOs;

import lombok.Getter;

import java.util.List;

public class BookTempCreatedDTO {
    @Getter
    private String isbn;
    @Getter
    private List<BookTempCreatedAuthorsDTO> authorsDTOs;

    public BookTempCreatedDTO(String isbn, List<BookTempCreatedAuthorsDTO> authorsDTOs) {
        this.isbn = isbn;
        this.authorsDTOs = authorsDTOs;
    }
}
