package pt.psoft.g1.psoftg1.bookmanagement.model.DTOs;

import lombok.Getter;

public class BookTempCreatedDTO {
    @Getter
    private String isbn;
    @Getter
    private String name;
    @Getter
    private String bio;

    public BookTempCreatedDTO(String isbn, String name, String bio ) {
        this.isbn = isbn;
        this.name = name;
        this.bio = bio;
    }
}
