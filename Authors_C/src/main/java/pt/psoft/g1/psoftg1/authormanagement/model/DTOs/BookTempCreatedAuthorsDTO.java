package pt.psoft.g1.psoftg1.authormanagement.model.DTOs;

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
