package pt.psoft.g1.psoftg1.bookmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private IdGenerator idGenerator;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- CREATE -------------------
    @Test
    void testCreateBook_Success() {
        // Arrange
        String isbn = "9789720706386";
        CreateBookRequest request = mock(CreateBookRequest.class);
        when(request.getTitle()).thenReturn("Book Title");
        when(request.getDescription()).thenReturn("Book Description");
        when(request.getAuthors()).thenReturn(List.of("1L"));
        when(request.getGenre()).thenReturn("Fiction");
        when(request.getPhoto()).thenReturn(null);
        when(request.getPhotoURI()).thenReturn(null);

        Author author = mock(Author.class);
        when(authorRepository.findByAuthorNumber("1L")).thenReturn(Optional.of(author));

        Genre genre = mock(Genre.class);
        when(genreRepository.findByString("Fiction")).thenReturn(Optional.of(genre));

        when(idGenerator.generateId()).thenReturn("123");

        Book savedBook = mock(Book.class);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        Book result = bookService.create(request, isbn);

        // Assert
        assertNotNull(result);
        assertEquals(savedBook, result);
    }

    @Test
    void testCreateBook_BookAlreadyExists() {
        // Arrange
        String isbn = "9789720706386";
        CreateBookRequest request = mock(CreateBookRequest.class);

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(mock(Book.class)));

        // Act + Assert
        assertThrows(ConflictException.class, () -> bookService.create(request, isbn));
    }

    @Test
    void testCreateBook_GenreNotFound() {
        // Arrange
        String isbn = "9789720706386";
        CreateBookRequest request = mock(CreateBookRequest.class);
        when(request.getGenre()).thenReturn("NonExistentGenre");
        when(request.getAuthors()).thenReturn(List.of("1L"));
        when(authorRepository.findByAuthorNumber("1L")).thenReturn(Optional.of(mock(Author.class)));
        when(genreRepository.findByString("NonExistentGenre")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> bookService.create(request, isbn));
    }

    // ------------------- UPDATE -------------------
    @Test
    void testUpdateBook_Success() {
        // Arrange
        String isbn = "1234567890";
        String currentVersion = "1";
        Author author = mock(Author.class);
        List<Author> authors = List.of(author);
        UpdateBookRequest request = mock(UpdateBookRequest.class);
        when(request.getGenre()).thenReturn("NonExistentGenre");
        when(request.getTitle()).thenReturn("Book Title");
        when(request.getDescription()).thenReturn("Book Description");
        when(request.getAuthorObjList()).thenReturn(authors);
        when(request.getGenre()).thenReturn("Fiction");
        when(request.getIsbn()).thenReturn(isbn);

        when(authorRepository.findByAuthorNumber("1L")).thenReturn(Optional.of(author));

        Genre genre = mock(Genre.class);
        when(genre.getGenre()).thenReturn("Fiction");
        when(genreRepository.findByString("Fiction")).thenReturn(Optional.of(genre));

        Book book = mock(Book.class);
        pt.psoft.g1.psoftg1.bookmanagement.model.Description description = mock(pt.psoft.g1.psoftg1.bookmanagement.model.Description.class);
        when(book.getTitle()).thenReturn(mock(Title.class));
        when(book.getTitle().getTitle()).thenReturn("Book Title");
        when(book.getDescription()).thenReturn(description);
        when(book.getGenre()).thenReturn(genre);
        when(book.getAuthors()).thenReturn(List.of(author));

        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(genreRepository.findByString("Fiction")).thenReturn(Optional.of(genre));

        // Act
        Book returned = bookService.update(request, currentVersion);

        // Assert
        assertNotNull(returned);
    }

    @Test
    void testUpdateBook_GenreNotFound() {
        //
        String isbn = "1234567890";
        String currentVersion = "1";
        UpdateBookRequest request = mock(UpdateBookRequest.class);
        when(request.getIsbn()).thenReturn(isbn);
        when(request.getGenre()).thenReturn("NonExistentGenre");
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(mock(Book.class)));
        when(genreRepository.findByString("NonExistentGenre")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookService.update(request, currentVersion));
    }

    // ------------------- REMOVE PHOTO -------------------
    @Test
    void testRemoveBookPhoto_Success() {
        String isbn = "9789720706386";
        Photo photo = mock(Photo.class);
        when(photo.getPhotoFile()).thenReturn("photo.jpg");

        Book book = mock(Book.class);
        when(book.getPhoto()).thenReturn(photo);
        when(book.getVersion()).thenReturn(1L);
        when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = bookService.removeBookPhoto(isbn, 1L);

        assertNotNull(result);
    }

    @Test
    void testRemoveBookPhoto_BookNotFound() {
        when(bookRepository.findByIsbn("nonexistentISBN")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.removeBookPhoto("nonexistentISBN", 1L));
    }

    // ------------------- FIND -------------------
    @Test
    void testFindByIsbn_BookExists() {
        Book book = mock(Book.class);
        Isbn isbn = mock(Isbn.class);
        when(isbn.getIsbn()).thenReturn("9789720706386");
        when(book.getIsbn()).thenReturn(isbn);
        when(bookRepository.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));

        Book result = bookService.findByIsbn("9789720706386");

        assertNotNull(result);
        assertEquals(isbn, result.getIsbn());
    }

    @Test
    void testFindByIsbn_BookNotFound() {
        when(bookRepository.findByIsbn("nonexistentISBN")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.findByIsbn("nonexistentISBN"));
    }

    @Test
    void testFindByGenre_Success() {
        String genreName = "Fiction";
        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn(mock(Title.class));
        when(book.getTitle().getTitle()).thenReturn("Title");
        List<Book> books = List.of(book);
        when(bookRepository.findByGenre(genreName)).thenReturn(books);

        List<Book> result = bookService.findByGenre(genreName);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ------------------- SUGGESTIONS -------------------
    // @Test
    // void testGetBooksSuggestionsForReader_Success() 
    // {
    //     // Arrange
    //     String readerNumber = "reader123";

    //     ReaderDetails reader = mock(ReaderDetails.class);
    //     Genre genre = mock(Genre.class);
    //     when(reader.getInterestList()).thenReturn(List.of(genre));
    //     when(readerRepository.findByReaderNumber(readerNumber)).thenReturn(Optional.of(reader));

    //     Book book = mock(Book.class);
    //     when(bookRepository.findByGenre(genre.toString())).thenReturn(List.of(book));

    //     // act
    //     List<Book> result = bookService.getBooksSuggestionsForReader(readerNumber);

    //     // Assert
    //     assertNotNull(result);
    //     assertEquals(1, result.size());
    // }

    @Test
    void testGetBooksSuggestionsForReader_ReaderNotFound() 
    {
        when(readerRepository.findByReaderNumber("unknownReader")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookService.getBooksSuggestionsForReader("unknownReader"));
    }

    // ------------------- TOP 5 BOOKS -------------------
    @Test
    void testFindTop5BooksLent_Success() 
    {
        BookCountDTO dto = mock(BookCountDTO.class);
        List<BookCountDTO> dtos = List.of(dto);

        when(bookRepository.findTop5BooksLent(any(LocalDate.class), any(PageRequest.class)))
                .thenReturn(dtos);

        List<BookCountDTO> result = bookService.findTop5BooksLent();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ------------------- SEARCH -------------------
    @Test
    void testSearchBooks_WithNullPageAndQuery() 
    {
        // Arrange
        SearchBooksQuery query = mock(SearchBooksQuery.class);
        when(query.getTitle()).thenReturn("Book Title 1");
        when(query.getAuthorName()).thenReturn("Author Name 1");
        when(query.getGenre()).thenReturn("Fiction");
        Book book1 = mock(Book.class);
        when(book1.getTitle()).thenReturn(mock(Title.class));
        when(book1.getTitle().getTitle()).thenReturn("Book Title 1");
        List<Book> expectedBooks = List.of(book1);

        when(bookRepository.searchBooks(any(Page.class), eq(query))).thenReturn(expectedBooks);

        // Act
        List<Book> result = bookService.searchBooks(null, query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Book Title 1", result.get(0).getTitle().getTitle());
    }

    @Test
    void testSearchBooks_WithDefaultPage() 
    {
        // Arrange
        SearchBooksQuery query = mock(SearchBooksQuery.class);
        Book book1 = mock(Book.class);
        when(book1.getTitle()).thenReturn(mock(Title.class));
        when(book1.getTitle().getTitle()).thenReturn("Book Title 1");
        List<Book> expectedBooks = List.of(book1);
        Page page = mock(Page.class);
        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);

        when(bookRepository.searchBooks(page, query)).thenReturn(expectedBooks);

        when(bookRepository.searchBooks(any(Page.class), eq(query))).thenReturn(expectedBooks);

        // Act
        List<Book> result = bookService.searchBooks(page, query);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Book Title 1", result.get(0).getTitle().getTitle());
    }
}
