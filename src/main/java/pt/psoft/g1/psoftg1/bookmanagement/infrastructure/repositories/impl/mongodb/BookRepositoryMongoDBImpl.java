package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.SpringDataAuthorRepositoryMongoDB;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookMapperMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreMapperMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.GenreRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.SpringDataGenreRepositoryMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@Repository
@RequiredArgsConstructor
public class BookRepositoryMongoDBImpl implements BookRepository {

    private final SpringDataBookRepositoryMongoDB bookRepositoryMongoDB;
    private final SpringDataGenreRepositoryMongoDB genreRepositoryMongoDB;

    private final SpringDataAuthorRepositoryMongoDB authorRepositoryMongoDB;
    private final BookMapperMongoDB bookMapperMongoDB;

    private final GenreRepositoryMongoDBImpl genreRepo;
    private final GenreMapperMongoDB genreMapperMongoDB;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public List<Book> findByGenre(@Param("genre") String genre)
    {
        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findByGenre(genre))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> findByTitle(@Param("title") String title)
    {
        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findByTitle(title))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> findByAuthorName(@Param("authorName") String authorName)
    {
        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findByAuthorName(authorName))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }

        return books;
    }

    @Override
    public Optional<Book> findByIsbn(@Param("isbn") String isbn)
    {
        Optional<BookMongoDB> entityOpt = bookRepositoryMongoDB.findByIsbn(isbn);
        if(entityOpt.isPresent())
        {
            return Optional.of(bookMapperMongoDB.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<BookCountDTO> findTop5BooksLent(@Param("oneYearAgo") LocalDate oneYearAgo, Pageable pageable)
    {
        //TODO: Corrigir este
        return bookRepositoryMongoDB.findTop5BooksLent(oneYearAgo, pageable);
    }

    @Override
    public List<Book> findBooksByAuthorNumber(String authorNumber)
    {
        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findBooksByAuthorNumber(authorNumber))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> searchBooks(pt.psoft.g1.psoftg1.shared.services.Page page, SearchBooksQuery query) {
        String title = query.getTitle();
        String genre = query.getGenre();
        String authorName = query.getAuthorName();

        Query mongoQuery = new Query();

        // Título
        if (title != null && !title.isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("title.title").regex("^" + title, "i"));
        }

        // Gênero
        if (genre != null && !genre.isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("genre.genre").regex("^" + genre, "i"));
        }

        // Nome do autor
        if (authorName != null && !authorName.isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("authors.name.name").regex("^" + authorName, "i"));
        }

        // Paginação
        Pageable pageable = PageRequest.of(page.getNumber() - 1, page.getLimit());
        mongoQuery.with(pageable);

        // Ordenação pelo título
        mongoQuery.with(Sort.by(Sort.Direction.ASC, "title.title"));

        // Buscar no MongoDB
        List<BookMongoDB> bookEntities = mongoTemplate.find(mongoQuery, BookMongoDB.class);

        // Mapear para o modelo de domínio
        return bookEntities.stream()
                .map(bookMapperMongoDB::toModel)
                .toList();
    }

//    @Override
//    public Book save(Book book) {
//        BookMongoDB bookMongoDB = bookMapperMongoDB.toMongoDB(book);
//
//        List<AuthorMongoDB> authors = bookMongoDB.getAuthors().stream()
//                .map(author -> authorRepositoryMongoDB
//                        .searchByNameName(author.getName().getName().toString())
//                        .stream()
//                        .findFirst()
//                        .orElseGet(() -> authorRepositoryMongoDB.save(author)))
//                .toList();
//
//        bookMongoDB.setAuthors(authors);
//
//        if (bookMongoDB.getGenre() != null) {
//            GenreMongoDB existingGenre = genreRepositoryMongoDB
//                    .findByString(bookMongoDB.getGenre().getGenre())
//                    .orElseGet(() -> genreRepositoryMongoDB.save(bookMongoDB.getGenre()));
//            bookMongoDB.setGenre(existingGenre);
//        }
//
//        BookMongoDB savedEntity = bookRepositoryMongoDB.save(bookMongoDB);
//        return bookMapperMongoDB.toModel(savedEntity);
//    }

    @Override
    public Book save(Book book) {
        // Converte o modelo de domínio para documento Mongo
        BookMongoDB bookDoc = bookMapperMongoDB.toMongoDB(book);

        // Verifica se o gênero existe
        Genre genreDoc = genreRepo.findByString(book.getGenre().getGenre())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        GenreMongoDB genreMongoDB = genreMapperMongoDB.toMongoDB(genreDoc);

        // Associa o gênero existente ao documento do livro
        bookDoc.setGenre(genreMongoDB);

        // Cria uma lista de autores existentes
        List<AuthorMongoDB> authors = new ArrayList<>();

        for (var author : book.getAuthors()) {
            // Busca o autor pelo nome
            // (em MongoDB geralmente retornamos lista, como antes)
            List<AuthorMongoDB> matches = authorRepositoryMongoDB.searchByNameName(author.getName().getName());
            if (matches.isEmpty()) {
                throw new RuntimeException("Author not found");
            }

            // Escolhe o primeiro autor encontrado (ou aplica outra lógica)
            AuthorMongoDB existingAuthor = matches.get(0);
            authors.add(existingAuthor);
        }

        // Associa os autores existentes
        bookDoc.setAuthors(authors);

        // Salva o documento no MongoDB
        BookMongoDB savedDoc = bookRepositoryMongoDB.save(bookDoc);

        // Retorna o modelo de domínio convertido de volta
        return bookMapperMongoDB.toModel(savedDoc);
    }


    @Override
    public void delete(Book book) {
        bookRepositoryMongoDB.delete(bookMapperMongoDB.toMongoDB(book));
    }
}