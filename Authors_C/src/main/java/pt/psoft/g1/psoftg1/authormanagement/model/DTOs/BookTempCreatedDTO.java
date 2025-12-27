package pt.psoft.g1.psoftg1.authormanagement.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
