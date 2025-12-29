package pt.psoft.g1.psoftg1.authormanagement.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class BookFinalizedDTO {
    @Getter
    private String isbn;
    @Getter
    private List<String> authorNumbers;
}
