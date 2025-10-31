package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.DTOs.BookDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreService;

import java.util.List;
import java.util.Optional;

public class BookRedisMapper {

    public static BookDTO toDto(Book book) {
        return new BookDTO(
                book.getBookId(),
                book.getVersion(),
                book.getIsbn().getIsbn(),
                book.getTitle().getTitle(),
                book.getDescription().getDescription(),
                book.getGenre().getGenre(),
                book.getAuthors().stream().map(Author::getAuthorNumber).toList(),
                book.getPhoto() != null ? book.getPhoto().getPhotoFile() : null
        );
    }

    public static Book toModel(BookDTO dto, Genre genre, List<Author> authors) {
        Book book = new Book(
                dto.getIsbn(),
                dto.getTitle(),
                dto.getDescription(),
                genre,
                authors,
                dto.getPhotoURI()
        );
        book.setBookId(dto.getBookId());
        return book;
    }
}
