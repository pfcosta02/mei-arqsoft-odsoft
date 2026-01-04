package pt.psoft.g1.psoftg1.bookmanagement.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookTempCreatedAuthorsDTO {
    @Getter
    private String name;
    @Getter
    private String bio;
}
