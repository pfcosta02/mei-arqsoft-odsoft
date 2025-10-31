package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.DTOs.BookDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.DTOs.GenreDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;

import java.util.List;
import java.util.Optional;

public class GenreRedisMapper {
    public static GenreDTO toDto(Genre genre) {
        return new GenreDTO(
                genre.getPk(),
                genre.getGenre()
        );
    }

    public static Genre toModel(GenreDTO dto) {

        Genre genre = new Genre(
                dto.getGenre()
        );

        genre.setPk(dto.getPk());
        return genre;
    }
}
