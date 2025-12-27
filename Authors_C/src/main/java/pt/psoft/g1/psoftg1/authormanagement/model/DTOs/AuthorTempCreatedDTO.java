package pt.psoft.g1.psoftg1.authormanagement.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorTempCreatedDTO {
    private String isbn;
    private List<String> authorNumber;
}
