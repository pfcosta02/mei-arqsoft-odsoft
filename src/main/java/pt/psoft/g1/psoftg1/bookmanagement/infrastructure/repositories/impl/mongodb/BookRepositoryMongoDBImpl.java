package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookMapperMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@Component
@RequiredArgsConstructor
public class BookRepositoryMongoDBImpl implements BookRepository {

    private final SpringDataBookRepositoryMongoDB bookRepositoryMongoDB;
    private final BookMapperMongoDB bookMapperMongoDB;
    private final EntityManager em;

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
    public Page<BookCountDTO> findTop5BooksLent(@Param("oneYearAgo") LocalDate oneYearAgo, Pageable pageable)
    {
        //TODO: Corrigir este
        return bookRepositoryMongoDB.findTop5BooksLent(oneYearAgo, pageable);
    }

    @Override
    public List<Book> findBooksByAuthorNumber(Long authorNumber)
    {
        List<Book> books = new ArrayList<>();
        for (BookMongoDB b: bookRepositoryMongoDB.findBooksByAuthorNumber(authorNumber))
        {
            books.add(bookMapperMongoDB.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> searchBooks(pt.psoft.g1.psoftg1.shared.services.Page page, SearchBooksQuery query)
    {
        String title = query.getTitle();
        String genre = query.getGenre();
        String authorName = query.getAuthorName();

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<BookMongoDB> cq = cb.createQuery(BookMongoDB.class);
        final Root<BookMongoDB> root = cq.from(BookMongoDB.class);
        final Join<BookMongoDB, Genre> genreJoin = root.join("genre");
        final Join<BookMongoDB, Author> authorJoin = root.join("authors");
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();

        if (StringUtils.hasText(title))
            where.add(cb.like(root.get("title").get("title"), title + "%"));

        if (StringUtils.hasText(genre))
            where.add(cb.like(genreJoin.get("genre"), genre + "%"));

        if (StringUtils.hasText(authorName))
            where.add(cb.like(authorJoin.get("name").get("name"), authorName + "%"));

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("title"))); // Order by title, alphabetically

        final TypedQuery<BookMongoDB> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List <Book> books = new ArrayList<>();

        for (BookMongoDB bookEntity : q.getResultList()) {
            books.add(bookMapperMongoDB.toModel(bookEntity));
        }

        return books;
    }

    @Override
    public Book save(Book book)
    {
        // TODO: implement save logic
        return null;
    }

    @Override
    public void delete(Book book)
    {
        // TODO: implement delete logic
    }
}