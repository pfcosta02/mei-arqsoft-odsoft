package pt.psoft.g1.psoftg1.bookmanagement.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb.AuthorRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookMapperMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb.BookRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb.SpringDataBookRepositoryMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreMapperMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.GenreRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.shared.model.Name;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookRepositoryMongoDBImplTest {
    @InjectMocks
    private BookRepositoryMongoDBImpl bookRepo;

    @Mock
    private SpringDataBookRepositoryMongoDB mongoRepo;

    @Mock
    private GenreRepositoryMongoDBImpl genreRepo;

    @Mock
    private AuthorRepositoryMongoDBImpl authorRepo;

    @Mock
    private BookMapperMongoDB bookEntityMapper;

    @Mock
    private GenreMapperMongoDB genreEntityMapper;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private EntityManager em;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(bookRepo, "mongoTemplate", mongoTemplate);
    }

    // ------------------- GETTERS -------------------
    @Test
    void testFindByGenre()
    {
        // Arrange
        Book mockBook = mock(Book.class);

        List<BookMongoDB> list = new ArrayList<>();
        BookMongoDB mockBookEntity = mock(BookMongoDB.class);
        list.add(mockBookEntity);

        when(mongoRepo.findByGenre(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findByGenre(anyString());

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindByGenreEmtpy()
    {
        // Arrange
        List<BookMongoDB> list = new ArrayList<>();

        when(mongoRepo.findByGenre(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findByGenre(anyString());

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.isEmpty());
    }

    @Test
    void testFindByTitle()
    {
        // Arrange
        Book mockBook = mock(Book.class);

        List<BookMongoDB> list = new ArrayList<>();
        BookMongoDB mockBookEntity = mock(BookMongoDB.class);
        list.add(mockBookEntity);

        when(mongoRepo.findByTitle(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findByTitle(anyString());

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindByTitleEmtpy()
    {
        // Arrange
        List<BookMongoDB> list = new ArrayList<>();

        when(mongoRepo.findByTitle(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findByTitle(anyString());

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.isEmpty());
    }

    @Test
    void testFindByAuthorNumber()
    {
        // Arrange
        Book mockBook = mock(Book.class);

        List<BookMongoDB> list = new ArrayList<>();
        BookMongoDB mockBookEntity = mock(BookMongoDB.class);
        list.add(mockBookEntity);

        when(mongoRepo.findByAuthorName(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findByAuthorName(anyString());

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindByAuthorNumberEmtpy()
    {
        // Arrange
        List<BookMongoDB> list = new ArrayList<>();

        when(mongoRepo.findByAuthorName(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findByAuthorName(anyString());

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.isEmpty());
    }

    @Test
    void testFindByIsbn()
    {
        // Arrange
        Book mockBook = mock(Book.class);
        BookMongoDB mockBookEntity = mock(BookMongoDB.class);

        when(mongoRepo.findByIsbn(anyString())).thenReturn(Optional.of(mockBookEntity));
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        Optional<Book> book = bookRepo.findByIsbn(anyString());

        // Assert
        assertNotNull(book);
        assertEquals(mockBook, book.get());
    }

    @Test
    void testFindByIsbnEmtpy()
    {
        // Arrange
        when(mongoRepo.findByIsbn(anyString())).thenReturn(Optional.empty());

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

        List<BookMongoDB> list = new ArrayList<>();
        BookMongoDB mockBookEntity = mock(BookMongoDB.class);
        list.add(mockBookEntity);

        when(mongoRepo.findBooksByAuthorNumber(anyString())).thenReturn(list);
        when(bookEntityMapper.toModel(mockBookEntity)).thenReturn(mockBook);

        // Act
        List<Book> books = bookRepo.findBooksByAuthorNumber(anyString());

        // Assert
        assertEquals(list.size(), books.size());
        assertTrue(books.contains(mockBook));
    }

    @Test
    void testFindBooksByAuthorNumberEmpty()
    {
        // Arrange
        List<BookMongoDB> list = new ArrayList<>();

        when(mongoRepo.findBooksByAuthorNumber(anyString())).thenReturn(list);

        // Act
        List<Book> books = bookRepo.findBooksByAuthorNumber(anyString());

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

        BookMongoDB bookEntity = mock(BookMongoDB.class);
        Book book = mock(Book.class);

        // Mock valores de entrada
        when(query.getTitle()).thenReturn("Harry");
        when(query.getGenre()).thenReturn("Fantasy");
        when(query.getAuthorName()).thenReturn("Rowling");

        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);

        when(mongoTemplate.find(any(Query.class), eq(BookMongoDB.class)))
                .thenReturn(List.of(bookEntity));

        // Mapper converte documento MongoDB para modelo de domínio
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

        when(query.getTitle()).thenReturn("");
        when(query.getGenre()).thenReturn("");
        when(query.getAuthorName()).thenReturn("");

        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);

        when(mongoTemplate.find(any(Query.class), eq(BookMongoDB.class)))
                .thenReturn(Collections.emptyList());

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

        BookMongoDB bookDoc  = mock(BookMongoDB.class);
        BookMongoDB savedDoc  = mock(BookMongoDB.class);
        GenreMongoDB genreMongoDB = mock(GenreMongoDB.class);

        when(bookEntityMapper.toMongoDB(book)).thenReturn(bookDoc);
        when(genreRepo.findByString("Fantasy")).thenReturn(Optional.of(genre));
        when(genreEntityMapper.toMongoDB(genre)).thenReturn(genreMongoDB);
        when(authorRepo.searchByNameName("Rowling")).thenReturn(List.of(author));
        when(mongoRepo.save(bookDoc)).thenReturn(savedDoc);
        when(bookEntityMapper.toModel(savedDoc)).thenReturn(book);

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

        when(bookEntityMapper.toMongoDB(book)).thenReturn(mock(BookMongoDB.class));
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

        when(bookEntityMapper.toMongoDB(book)).thenReturn(mock(BookMongoDB.class));
        when(genreRepo.findByString("Fantasy")).thenReturn(Optional.of(genre));
        when(em.getReference(GenreMongoDB.class, 1L)).thenReturn(mock(GenreMongoDB.class));
        when(authorRepo.searchByNameName("Unknown")).thenReturn(Collections.emptyList());

        // Act + Assert
        assertThrows(RuntimeException.class, () -> bookRepo.save(book));
    }
}
