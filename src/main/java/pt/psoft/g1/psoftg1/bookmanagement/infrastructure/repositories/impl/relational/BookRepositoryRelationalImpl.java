package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.AuthorRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookRedisMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.DTOs.BookDTO;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational.GenreRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.redis.RedisCacheRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class BookRepositoryRelationalImpl implements BookRepository
{
    private final SpringDataBookRepository bookRepo;
    private final BookEntityMapper bookEntityMapper;
    private final GenreRepositoryRelationalImpl genreRepo;
    private final AuthorRepositoryRelationalImpl authorRepo;
    private final EntityManager em;
    private final RedisCacheRepository cache;

    @Override
    public List<Book> findByGenre(@Param("genre") String genre)
    {
        String cacheKey = "books:genre:" + genre.toLowerCase();
        List<BookDTO> cachedBookDTOs = cache.findList(cacheKey, BookDTO.class).orElse(null);
        if (cachedBookDTOs != null) {
            return cachedBookDTOs.stream()
                    .map(dto -> {
                        Genre genre_ = genreRepo.findByString(dto.getgenreName())
                                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + dto.getgenreName()));

                        List<Author> authors = dto.getAuthors().stream()
                                .map(authorRepo::findByAuthorNumber)
                                .flatMap(Optional::stream)
                                .toList();

                        return BookRedisMapper.toModel(dto, genre_, authors);
                    })
                    .toList();
        }


        List<Book> books = new ArrayList<>();
        for (BookEntity b: bookRepo.findByGenre(genre))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        List<BookDTO> bookDTOList = books.stream().map(BookRedisMapper::toDto).toList();
        cache.save(cacheKey, bookDTOList);

        return books;
    }

    @Override
    public List<Book> findByTitle(@Param("title") String title)
    {
        String cacheKey = "books:title:" + title.toLowerCase();
        List<BookDTO> cachedBookDTOs = cache.findList(cacheKey, BookDTO.class).orElse(null);
        if (cachedBookDTOs != null) {
            return cachedBookDTOs.stream()
                    .map(dto -> {
                        Genre genre_ = genreRepo.findByString(dto.getgenreName())
                                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + dto.getgenreName()));

                        List<Author> authors = dto.getAuthors().stream()
                                .map(authorRepo::findByAuthorNumber)
                                .flatMap(Optional::stream)
                                .toList();

                        return BookRedisMapper.toModel(dto, genre_, authors);
                    })
                    .toList();
        }

        List<Book> books = new ArrayList<>();
        for (BookEntity b: bookRepo.findByTitle(title))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        List<BookDTO> bookDTOList = books.stream().map(BookRedisMapper::toDto).toList();
        cache.save(cacheKey, bookDTOList);

        return books;
    }

    @Override
    public List<Book> findByAuthorName(@Param("authorName") String authorName)
    {
        String cacheKey = "books:title:" + authorName.toLowerCase();
        List<BookDTO> cachedBookDTOs = cache.findList(cacheKey, BookDTO.class).orElse(null);
        if (cachedBookDTOs != null) {
            return cachedBookDTOs.stream()
                    .map(dto -> {
                        Genre genre_ = genreRepo.findByString(dto.getgenreName())
                                .orElseThrow(() -> new IllegalArgumentException("Genre not found: " + dto.getgenreName()));

                        List<Author> authors = dto.getAuthors().stream()
                                .map(authorRepo::findByAuthorNumber)
                                .flatMap(Optional::stream)
                                .toList();

                        return BookRedisMapper.toModel(dto, genre_, authors);
                    })
                    .toList();
        }

        List<Book> books = new ArrayList<>();
        for (BookEntity b: bookRepo.findByAuthorName(authorName))
        {
            books.add(bookEntityMapper.toModel(b));
        }

        List<BookDTO> bookDTOList = books.stream().map(BookRedisMapper::toDto).toList();
        cache.save(cacheKey, bookDTOList);

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
    public List<BookCountDTO> findTop5BooksLent(@Param("oneYearAgo") LocalDate oneYearAgo, Pageable pageable)
    {
        //TODO: Corrigir este
        return bookRepo.findTop5BooksLent(oneYearAgo, pageable);
    }

    @Override
    public List<Book> findBooksByAuthorNumber(String authorNumber)
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
    @Transactional
    public Book save(Book book)
    {
        // Convert the domain model (Book) to a JPA entity (BookEntity)
        BookEntity entity = bookEntityMapper.toEntity(book);

        // Retrieve the existing Genre model from the repository
        // Throws an exception if the genre is not found
        Genre genreModel = genreRepo.findByString(book.getGenre().getGenre())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        // Get the managed JPA reference for the GenreEntity using its database ID (pk)
        // This ensures we use the existing GenreEntity instead of creating a new one
        GenreEntity genreEntity = em.getReference(GenreEntity.class, genreModel.getPk());

        // Set the managed GenreEntity on the BookEntity
        entity.setGenre(genreEntity);

        // Prepare a list to hold managed AuthorEntity instances
        List<AuthorEntity> authors = new ArrayList<>();

        // For each author in the Book model
        for (var author : book.getAuthors())
        {
            // Retrieve the corresponding Author model from the repository by author number
            //TODO: temos aqui uma questao, o searchByNameName retorna uma lista de nomes, entao pode nao ser o autor correto (no caso de haver varios autores com o mesmo nome)
            List<Author> auth1  = authorRepo.searchByNameName(author.getName().getName());
            Author auth = auth1.get(0);
            if (auth == null)
            {
                throw new RuntimeException("Author not found");
            }

            // Get a managed reference to the existing AuthorEntity by its author number
            AuthorEntity authorEntity = em.getReference(AuthorEntity.class, auth.getAuthorNumber());

            // Add the managed AuthorEntity to the list
            authors.add(authorEntity);
        }

        // Associate all managed AuthorEntity objects with the BookEntity
        entity.setAuthors(authors);

        // Persist the BookEntity and return the saved Book as a domain model
        BookEntity saved = bookRepo.save(entity);
        return bookEntityMapper.toModel(saved);
    }

    @Override
    public void delete(Book book)
    {
        // TODO: implement delete logic
    }
}


