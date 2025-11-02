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
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.redis.BookRepositoryRedisImpl;
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

    private final BookRepositoryRedisImpl redisRepo;

    private static final String PREFIX = "books:";

    @Autowired
    private MongoTemplate mongoTemplate;


    @Override
    public List<Book> findByGenre(@Param("genre") String genre)
    {
        List<Book> cached = redisRepo.getBookListFromRedis(PREFIX + "genre:" + genre);
        if (!cached.isEmpty()) return cached;

        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findByGenre(genre))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }
        redisRepo.cacheBookListToRedis(PREFIX + "genre:" + genre, books);
        return books;
    }

    @Override
    public List<Book> findByTitle(@Param("title") String title)
    {
        List<Book> cached = redisRepo.getBookListFromRedis(PREFIX + "title:" + title);
        if (!cached.isEmpty()) return cached;

        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findByTitle(title))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }
        redisRepo.cacheBookListToRedis(PREFIX + "title:" + title, books);
        return books;
    }

    @Override
    public List<Book> findByAuthorName(@Param("authorName") String authorName)
    {
        List<Book> cached = redisRepo.getBookListFromRedis(PREFIX + "authorName:" + authorName);
        if (!cached.isEmpty()) return cached;

        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findByAuthorName(authorName))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }
        redisRepo.cacheBookListToRedis(PREFIX + "authorName:" + authorName, books);
        return books;
    }

    @Override
    public Optional<Book> findByIsbn(@Param("isbn") String isbn)
    {
        Optional<Book> cached = redisRepo.getBookFromRedis(PREFIX + "isbn:" + isbn);
        if (cached.isPresent()) return cached;

        Optional<BookMongoDB> entityOpt = bookRepositoryMongoDB.findByIsbn(isbn);
        if(entityOpt.isPresent())
        {
            Book book = bookMapperMongoDB.toModel(entityOpt.get());
            redisRepo.save(book);
            return Optional.of(book);
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<BookCountDTO> findTop5BooksLent(@Param("oneYearAgo") LocalDate oneYearAgo, Pageable pageable)
    {
        return bookRepositoryMongoDB.findTop5BooksLent(oneYearAgo, pageable);
    }

    @Override
    public List<Book> findBooksByAuthorNumber(String authorNumber)
    {
        List<Book> cached = redisRepo.getBookListFromRedis(PREFIX + "authorNumber:" + authorNumber);
        if (!cached.isEmpty()) return cached;

        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findBooksByAuthorNumber(authorNumber))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }
        redisRepo.cacheBookListToRedis(PREFIX + "authorNumber:" + authorNumber, books);
        return books;
    }

    @Override
    public List<Book> searchBooks(pt.psoft.g1.psoftg1.shared.services.Page page, SearchBooksQuery query) {
        String title = query.getTitle();
        String genre = query.getGenre();
        String authorName = query.getAuthorName();

        Query mongoQuery = new Query();

        if (title != null && !title.isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("title.title").regex("^" + title, "i"));
        }

        if (genre != null && !genre.isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("genre.genre").regex("^" + genre, "i"));
        }

        if (authorName != null && !authorName.isEmpty()) {
            mongoQuery.addCriteria(Criteria.where("authors.name.name").regex("^" + authorName, "i"));
        }

        Pageable pageable = PageRequest.of(page.getNumber() - 1, page.getLimit());
        mongoQuery.with(pageable);

        mongoQuery.with(Sort.by(Sort.Direction.ASC, "title.title"));

        List<BookMongoDB> bookEntities = mongoTemplate.find(mongoQuery, BookMongoDB.class);

        return bookEntities.stream()
                .map(bookMapperMongoDB::toModel)
                .toList();
    }

    @Override
    public Book save(Book book) {
        BookMongoDB bookDoc = bookMapperMongoDB.toMongoDB(book);

        Genre genreDoc = genreRepo.findByString(book.getGenre().getGenre())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        GenreMongoDB genreMongoDB = genreMapperMongoDB.toMongoDB(genreDoc);

        bookDoc.setGenre(genreMongoDB);

        List<AuthorMongoDB> authors = new ArrayList<>();

        for (var author : book.getAuthors()) {
            List<AuthorMongoDB> matches = authorRepositoryMongoDB.searchByNameName(author.getName().getName());
            if (matches.isEmpty()) {
                throw new RuntimeException("Author not found");
            }

            AuthorMongoDB existingAuthor = matches.get(0);
            authors.add(existingAuthor);
        }

        bookDoc.setAuthors(authors);

        BookMongoDB savedDoc = bookRepositoryMongoDB.save(bookDoc);

        redisRepo.save(bookMapperMongoDB.toModel(savedDoc));
        // Retorna o modelo de domínio convertido de volta
        return bookMapperMongoDB.toModel(savedDoc);
    }

    @Override
    public void delete(Book book) {
        bookRepositoryMongoDB.delete(bookMapperMongoDB.toMongoDB(book));
    }
}