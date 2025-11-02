package pt.psoft.g1.psoftg1.bookmanagement.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.AuthorRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.redis.BookRepositoryRedisImpl;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational.BookRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational.SpringDataBookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational.GenreRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;

/* Teste Unitario, opaque-box do BookRepositoryImpl */
class BookRepositoryRelationalImplTest
{
    @InjectMocks
    private BookRepositoryRelationalImpl bookRepo;

    @Mock
    private SpringDataBookRepository sqlRepo;

    @Mock
    private GenreRepositoryRelationalImpl genreRepo;

    @Mock
    private AuthorRepositoryRelationalImpl authorRepo;

    @Mock
    private BookRepositoryRedisImpl redisRepo;

    @Mock
    private BookEntityMapper bookEntityMapper;

    @Mock
    private EntityManager em;

    private static final String key = "Key";

    @BeforeEach
    void setUp() 
    {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- GETTERS -------------------
    @Test
    void testFindByGenre()
    {
        // Arrange
        Book mockBook = mock(Book.class);
        List<Book> cached = new ArrayList<>();
        cached.add(mockBook);

        List<BookEntity> list = new ArrayList<>();
        BookEntity mockBookEntity = mock(BookEntity.class);
        list.add(mockBookEntity);

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findByGenre(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findByGenre(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindByGenreEmtpy()
    {
        // Arrange
        List<BookEntity> list = new ArrayList<>();
        List<Book> cached = new ArrayList<>();

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findByGenre(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findByGenre(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.isEmpty());
    }

    @Test
    void testFindByTitle()
    {
        // Arrange
        Book mockBook = mock(Book.class);
        List<Book> cached = new ArrayList<>();
        cached.add(mockBook);

        List<BookEntity> list = new ArrayList<>();
        BookEntity mockBookEntity = mock(BookEntity.class);
        list.add(mockBookEntity);

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findByTitle(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findByTitle(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindByTitleEmtpy()
    {
        // Arrange
        List<BookEntity> list = new ArrayList<>();
        List<Book> cached = new ArrayList<>();

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findByTitle(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findByTitle(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.isEmpty());
    }

    @Test
    void testFindByAuthorNumber()
    {
        // Arrange
        Book mockBook = mock(Book.class);
        List<Book> cached = new ArrayList<>();
        cached.add(mockBook);

        List<BookEntity> list = new ArrayList<>();
        BookEntity mockBookEntity = mock(BookEntity.class);
        list.add(mockBookEntity);

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findByAuthorName(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findByAuthorName(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindByAuthorNumberEmtpy()
    {
        // Arrange
        List<BookEntity> list = new ArrayList<>();
        List<Book> cached = new ArrayList<>();

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findByAuthorName(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findByAuthorName(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.isEmpty());
    }

    @Test
    void testFindByIsbn()
    {
        // Arrange
        Book mockBook = mock(Book.class);
        BookEntity mockBookEntity = mock(BookEntity.class);
        Optional<Book> mockOptBook = Optional.of(mockBook);

        when(redisRepo.getBookFromRedis(key)).thenReturn(mockOptBook);
        when(sqlRepo.findByIsbn(anyString())).thenReturn(Optional.of(mockBookEntity));
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        Optional<Book> book = bookRepo.findByIsbn(anyString());
        doNothing().when(redisRepo).save(mockBook);

        // Assert
        assertNotNull(book);
        assertEquals(mockBook, book.get());
    }

    @Test
    void testFindByIsbnEmtpy()
    {
        // Arrange
        when(redisRepo.getBookFromRedis(anyString())).thenReturn(Optional.empty());
        when(sqlRepo.findByIsbn(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<Book> book = bookRepo.findByIsbn(anyString());

        // Assert
        assertEquals(Optional.empty(), book);
    }

    @Test
    void testFindTop5BooksLent()
    {
        // Arrange
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        Pageable pageable = PageRequest.of(0, 10);

        BookCountDTO dto = mock(BookCountDTO.class);
        List<BookCountDTO> list = List.of(dto);

        when(bookRepo.findTop5BooksLent(oneYearAgo, pageable)).thenReturn(list);

        // Act
        List<BookCountDTO> page = bookRepo.findTop5BooksLent(oneYearAgo, pageable);

        // Assert
        assertEquals(list.size(), page.size());
    }

    @Test
    void testFindBooksByAuthorNumber()
    {
        // Arrange
        Book mockBook = mock(Book.class);
        List<Book> cached = new ArrayList<>();
        cached.add(mockBook);

        List<BookEntity> list = new ArrayList<>();
        BookEntity mockBookEntity = mock(BookEntity.class);
        list.add(mockBookEntity);

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findBooksByAuthorNumber(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findBooksByAuthorNumber(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindBooksByAuthorNumberEmpty()
    {
        // Arrange
        List<BookEntity> list = new ArrayList<>();
        List<Book> cached = new ArrayList<>();

        when(redisRepo.getBookListFromRedis(key)).thenReturn(cached);
        when(sqlRepo.findBooksByAuthorNumber(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findBooksByAuthorNumber(anyString());
        doNothing().when(redisRepo).cacheBookListToRedis(key, books);

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.isEmpty());
    }

    @Test
    void testSearchBooksWithAllFields() 
    {
        // Arrange
        SearchBooksQuery query = mock(SearchBooksQuery.class);
        pt.psoft.g1.psoftg1.shared.services.Page page = mock(pt.psoft.g1.psoftg1.shared.services.Page.class);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<BookEntity> cq = mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Root<BookEntity> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        Join<Object, Object> genreJoin = mock(Join.class);
        @SuppressWarnings("unchecked")
        Join<Object, Object> authorJoin = mock(Join.class);
        @SuppressWarnings("unchecked")
        TypedQuery<BookEntity> typedQuery = mock(TypedQuery.class);

        BookEntity bookEntity = mock(BookEntity.class);
        Book book = mock(Book.class);

        // Mock valores de entrada
        when(query.getTitle()).thenReturn("Harry");
        when(query.getGenre()).thenReturn("Fantasy");
        when(query.getAuthorName()).thenReturn("Rowling");

        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);

        // Mock encadeamento para root.get("title").get("title")
        @SuppressWarnings("unchecked")
        Path<Object> titlePath = (Path<Object>) mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<String> titleFinalPath = (Path<String>) mock(Path.class);
        when(root.get("title")).thenReturn(titlePath);
        when(titlePath.get("title")).thenReturn((Path) titleFinalPath);
        Predicate titlePredicate = mock(Predicate.class);
        when(cb.like(titleFinalPath, "Harry%")).thenReturn(titlePredicate);

        // Mock encadeamento para genreJoin.get("genre")
        @SuppressWarnings("unchecked")
        Path<String> genrePath = (Path<String>) mock(Path.class);
        Predicate genrePredicate = mock(Predicate.class);
        when(genreJoin.get("genre")).thenReturn((Path)genrePath);
        when(cb.like(genrePath, "Fantasy%")).thenReturn(genrePredicate);

        // Mock encadeamento para authorJoin.get("name").get("name")
        @SuppressWarnings("unchecked")
        Path<Object> authorNamePath = (Path<Object>) mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<String> authorFinalPath = (Path<String>) mock(Path.class);
        Predicate authorPredicate = mock(Predicate.class);
        when(authorJoin.get("name")).thenReturn(authorNamePath);
        when(authorNamePath.get("name")).thenReturn((Path) authorFinalPath);
        when(cb.like(authorFinalPath, "Rowling%")).thenReturn(authorPredicate);

        // Mock construção da query
        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(BookEntity.class)).thenReturn(cq);
        when(cq.from(BookEntity.class)).thenReturn(root);
        when(root.join("genre")).thenReturn(genreJoin);
        when(root.join("authors")).thenReturn(authorJoin);
        when(cq.select(root)).thenReturn(cq);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cq.orderBy(anyList())).thenReturn(cq);

        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(0)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(10)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(bookEntity));
        when(bookEntityMapper.toModel(bookEntity)).thenReturn(book);

        // Act
        List<Book> result = bookRepo.searchBooks(page, query);

        // Assert
        assertEquals(1, result.size());
        assertEquals(book, result.get(0));
    }


    @Test
    void testSearchBooksWithEmptyFields() 
    {
        // Arrange
        SearchBooksQuery query = mock(SearchBooksQuery.class);
        pt.psoft.g1.psoftg1.shared.services.Page page = mock(pt.psoft.g1.psoftg1.shared.services.Page.class);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<BookEntity> cq = mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Root<BookEntity> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        Join<Object, Object> genreJoin = mock(Join.class);
        @SuppressWarnings("unchecked")
        Join<Object, Object> authorJoin = mock(Join.class);
        @SuppressWarnings("unchecked")
        TypedQuery<BookEntity> typedQuery = mock(TypedQuery.class);

        when(query.getTitle()).thenReturn("");
        when(query.getGenre()).thenReturn("");
        when(query.getAuthorName()).thenReturn("");

        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(BookEntity.class)).thenReturn(cq);
        when(cq.from(BookEntity.class)).thenReturn(root);
        when(root.join("genre")).thenReturn(genreJoin);
        when(root.join("authors")).thenReturn(authorJoin);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<Book> result = bookRepo.searchBooks(page, query);

        // Assert
        assertTrue(result.isEmpty());
    }

    // CREATE
    @Test
    void testSaveBookSuccessfully() 
    {
        // Arrange
        Genre genre = mock(Genre.class);
        when(genre.getGenre()).thenReturn("Fantasy");
        Author author = mock(Author.class);
        
        Book book = mock(Book.class);
        when(book.getGenre()).thenReturn(genre);

        BookEntity entity = mock(BookEntity.class);
        BookEntity savedEntity = mock(BookEntity.class);

        when(bookEntityMapper.toEntity(book)).thenReturn(entity);
        when(genreRepo.findByString("Fantasy")).thenReturn(Optional.of(genre));
        when(em.getReference(GenreEntity.class, 1L)).thenReturn(mock(GenreEntity.class));
        when(authorRepo.searchByNameName("Rowling")).thenReturn(List.of(author));
        when(em.getReference(AuthorEntity.class, 100L)).thenReturn(mock(AuthorEntity.class));
        when(sqlRepo.save(entity)).thenReturn(savedEntity);
        when(bookEntityMapper.toModel(savedEntity)).thenReturn(book);
        doNothing().when(redisRepo).save(book);

        // Act
        Book result = bookRepo.save(book);

        // Assert
        assertEquals(book, result);
    }

    @Test
    void testSaveBookGenreNotFound() 
    {
        // Arrange
        Book book = mock(Book.class);

        when(bookEntityMapper.toEntity(book)).thenReturn(mock(BookEntity.class));
        when(genreRepo.findByString("Unknown")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> bookRepo.save(book));
    }

    @Test
    void testSaveBookAuthorNotFound() 
    {
        // Arrange
        Genre genre = mock(Genre.class);
        Book book = mock(Book.class);

        when(bookEntityMapper.toEntity(book)).thenReturn(mock(BookEntity.class));
        when(genreRepo.findByString("Fantasy")).thenReturn(Optional.of(genre));
        when(em.getReference(GenreEntity.class, 1L)).thenReturn(mock(GenreEntity.class));
        when(authorRepo.searchByNameName("Unknown")).thenReturn(Collections.emptyList());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> bookRepo.save(book));
    }
}
