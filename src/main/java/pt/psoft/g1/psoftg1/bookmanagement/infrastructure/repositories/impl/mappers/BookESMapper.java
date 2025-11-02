package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorESMapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.elasticsearch.AuthorES;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch.BookES;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.elasticsearch.GenreES;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoMapperES;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = { PhotoMapperES.class, AuthorESMapper.class })
public interface BookESMapper {

    default BookES toEntity(Book book) {
        if (book == null) return null;

        BookES entity = new BookES();

        // Mapear campos simples
        entity.setId(book.getIsbn().getIsbn());  // ISBN como ID
        entity.setIsbn(book.getIsbn().getIsbn());
        entity.setTitle(book.getTitle().toString());
        entity.setDescription(book.getDescription().getDescription());
        entity.setVersion(book.getVersion());

        // Mapear Genre
        if (book.getGenre() != null) {
            entity.setGenre(genreToGenreES(book.getGenre()));
        }

        // Mapear Authors
        if (book.getAuthors() != null && !book.getAuthors().isEmpty()) {
            entity.setAuthors(authorsToAuthorsES(book.getAuthors()));
        }

        // Mapear Photo
        if (book.getPhoto() != null && book.getPhoto().getPhotoFile() != null) {
            entity.setPhoto(book.getPhoto().getPhotoFile());
        }

        return entity;
    }

    default Book toModel(BookES entity) {
        if (entity == null) return null;

        // Converter Genre
        Genre genre = genreESToGenre(entity.getGenre());

        // Converter Authors
        List<Author> authors = authorsESToAuthors(entity.getAuthors());

        // Criar Book
        String photo = entity.getPhoto() != null ? entity.getPhotoFile() : null;

        return new Book(
                entity.getIsbn(),
                entity.getTitle(),
                entity.getDescription(),
                genre,
                authors,
                photo
        );
    }

    // Conversão Genre
    default GenreES genreToGenreES(Genre genre) {
        if (genre == null) return null;
        return new GenreES(genre.getGenre());
    }

    default Genre genreESToGenre(GenreES genreES) {
        if (genreES == null) return null;
        return new Genre(genreES.getGenre());
    }

    // Conversão Authors (usa AuthorESMapper)
    default List<AuthorES> authorsToAuthorsES(List<Author> authors) {
        if (authors == null) return null;
        return authors.stream()
                .map(this::authorToAuthorES)
                .collect(Collectors.toList());
    }

    default List<Author> authorsESToAuthors(List<AuthorES> authorsES) {
        if (authorsES == null) return null;
        return authorsES.stream()
                .map(this::authorESToAuthor)
                .collect(Collectors.toList());
    }

    // Conversão individual de Author
    default AuthorES authorToAuthorES(Author author) {
        if (author == null) return null;

        AuthorES entity = new AuthorES();
        entity.setAuthorNumber(author.getAuthorNumber());
        entity.setName(author.getName().getName());
        entity.setBio(author.getBio().getBio());

        if (author.getPhoto() != null && author.getPhoto().getPhotoFile() != null) {
            entity.setPhoto(author.getPhoto().getPhotoFile());
        }

        return entity;
    }

    default Author authorESToAuthor(AuthorES entity) {
        if (entity == null) return null;

        return new Author(
                entity.getName(),
                entity.getBio(),
                entity.getPhoto() != null ? entity.getPhotoFile() : null
        );
    }
}