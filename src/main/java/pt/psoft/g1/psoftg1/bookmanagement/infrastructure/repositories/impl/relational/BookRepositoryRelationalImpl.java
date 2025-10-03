package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Profile("jpa")
@Primary
@RequiredArgsConstructor
public class BookRepositoryRelationalImpl implements BookRepository
{
    private final SpringDataBookRepository bookRepo;
    private final BookEntityMapper bookEntityMapper;
    private final EntityManager em;

    @Override
    public List<Book> findByGenre(@Param("genre") String genre)
    {
        List<Book> books = new ArrayList<>();
        for (BookEntity b: bookRepo.findByGenre(genre))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> findByTitle(@Param("title") String title)
    {
        List<Book> books = new ArrayList<>();
        for (BookEntity b: bookRepo.findByTitle(title))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        return books;
    }

    @Override
    public List<Book> findByAuthorName(@Param("authorName") String authorName)
    {
        List<Book> books = new ArrayList<>();
        for (BookEntity b: bookRepo.findByAuthorName(authorName))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        return books;
    }

    @Override
    public Optional<Book> findByIsbn(@Param("isbn") String isbn)
    {
        Optional<BookEntity> entityOpt = bookRepo.findByIsbn(isbn);
        if(entityOpt.isPresent())
        {
            return Optional.of(bookEntityMapper.toModel(entityOpt.get()));
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
        return bookRepo.findTop5BooksLent(oneYearAgo, pageable);
    }

    @Override
    public List<Book> findBooksByAuthorNumber(Long authorNumber)
    {
        List<Book> books = new ArrayList<>();
        for (BookEntity b: bookRepo.findBooksByAuthorNumber(authorNumber))
        {
            books.add(bookEntityMapper.toModel(b));
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
        final CriteriaQuery<BookEntity> cq = cb.createQuery(BookEntity.class);
        final Root<BookEntity> root = cq.from(BookEntity.class);
        final Join<BookEntity, Genre> genreJoin = root.join("genre");
        final Join<BookEntity, Author> authorJoin = root.join("authors");
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

        final TypedQuery<BookEntity> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List <Book> books = new ArrayList<>();

        for (BookEntity bookEntity : q.getResultList()) {
            books.add(bookEntityMapper.toModel(bookEntity));
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


