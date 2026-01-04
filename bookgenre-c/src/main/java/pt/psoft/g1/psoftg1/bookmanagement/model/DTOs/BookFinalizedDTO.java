package pt.psoft.g1.psoftg1.bookmanagement.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
public class BookFinalizedDTO {
    @Getter
    private String isbn;
    @Getter
    private List<String> authorNumbers;
}
