package pt.psoft.g1.psoftg1.authormanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/* Teste Unitario, opaque-box do AuthorServiceImpl */
class AuthorServiceImplTest {
    @InjectMocks
    private AuthorServiceImpl authorService;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorMapper authorMapper;

    @Mock
    private PhotoRepository photoRepository;

    @BeforeEach
    void setUp() 
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByAuthorNumber() {
        String authorNumber = "1L";
        Author author = mock(Author.class);
        when(author.getAuthorNumber()).thenReturn(authorNumber);
        when(authorRepository.findByAuthorNumber(authorNumber)).thenReturn(Optional.of(author));

        Optional<Author> result = authorService.findByAuthorNumber(authorNumber);

        assertTrue(result.isPresent());
        assertEquals(author, result.get());
    }

    @Test
    void testFindByName() {
        String name = "AuthorName";
        Author mockAuthor = mock(Author.class);
        Name mockName = mock(Name.class);
        when(mockAuthor.getName()).thenReturn(mockName);
        when(mockName.getName()).thenReturn(name);

        List<Author> authors = List.of(mockAuthor);
        when(authorRepository.searchByNameNameStartsWith(name)).thenReturn(authors);

        List<Author> result = authorService.findByName(name);

        assertNotNull(result);
        assertEquals(authors, result);
    }

    @Test
    void testCreateAuthor() {
        CreateAuthorRequest request = mock(CreateAuthorRequest.class);
        Author author = mock(Author.class);
        when(authorMapper.create(request)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);

        Author result = authorService.create(request);

        assertEquals(author, result);
    }

    @Test
    void createAuthorWithNullPhoto() {
        CreateAuthorRequest request = mock(CreateAuthorRequest.class);
        when(request.getPhoto()).thenReturn(null);
        when(request.getPhotoURI()).thenReturn("photo.jpg");
        Author author = mock(Author.class);
        when(authorMapper.create(request)).thenReturn(author);
        when(authorRepository.save(author)).thenReturn(author);

        Author result = authorService.create(request);

        assertEquals(author, result);
    }

    @Test
    void testPartialUpdateSuccess() {
        String authorNumber = "1L";
        UpdateAuthorRequest updateRequest = mock(UpdateAuthorRequest.class);
        long desiredVersion = 1L;

        Author author = mock(Author.class);
        when(authorRepository.findByAuthorNumber(authorNumber)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);

        Author result = authorService.partialUpdate(authorNumber, updateRequest, desiredVersion);

        assertEquals(author, result);
    }

    @Test
    void testPartialUpdateWithNullPhoto() {
        String authorNumber= "1L";
        UpdateAuthorRequest updateRequest = mock(UpdateAuthorRequest.class);
        when(updateRequest.getPhoto()).thenReturn(null);
        when(updateRequest.getPhotoURI()).thenReturn("photo.jpg");
        long desiredVersion = 1L;

        Author author = mock(Author.class);
        when(authorRepository.findByAuthorNumber(authorNumber)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).thenReturn(author);

        Author result = authorService.partialUpdate(authorNumber, updateRequest, desiredVersion);

        assertEquals(author, result);
    }

    @Test
    void testPartialUpdateAuthorNotFound() 
    {
        String authorNumber = "1L";
        UpdateAuthorRequest updateRequest = mock(UpdateAuthorRequest.class);
        long desiredVersion = 1L;

        when(authorRepository.findByAuthorNumber(authorNumber)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authorService.partialUpdate(authorNumber, updateRequest, desiredVersion));
    }

    @Test
    void testFindTopAuthorByLendings() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<AuthorLendingView> authors = List.of(mock(AuthorLendingView.class));
        when(authorRepository.findTopAuthorByLendings(pageRequest)).thenReturn(authors);

        List<AuthorLendingView> result = authorService.findTopAuthorByLendings();

        assertEquals(authors, result);
    }

    @Test
    void testFindBooksByAuthorNumber() {
        String authorNumber = "1L";
        List<Book> books = List.of(mock(Book.class));
        when(bookRepository.findBooksByAuthorNumber(authorNumber)).thenReturn(books);

        List<Book> result = authorService.findBooksByAuthorNumber(authorNumber);

        assertEquals(books, result);
    }

    @Test
    void testFindCoAuthorsByAuthorNumber() {
        String authorNumber = "1L";
        List<Author> coAuthors = List.of(mock(Author.class));
        when(authorRepository.findCoAuthorsByAuthorNumber(authorNumber)).thenReturn(coAuthors);

        List<Author> result = authorService.findCoAuthorsByAuthorNumber(authorNumber);

        assertEquals(coAuthors, result);
    }

    @Test
    void testRemoveAuthorPhoto() {
        String authorNumber = "1L";
        long desiredVersion = 1L;
        Author author = mock(Author.class);
        Photo photo = mock(Photo.class);
        when(authorRepository.findByAuthorNumber(authorNumber)).thenReturn(Optional.of(author));
        when(author.getPhoto()).thenReturn(photo);
        when(photo.getPhotoFile()).thenReturn("photo.jpg");
        when(authorRepository.save(author)).thenReturn(author);

        Optional<Author> result = authorService.removeAuthorPhoto(authorNumber, desiredVersion);

        assertTrue(result.isPresent());
        assertEquals(author, result.get());
    }

    @Test
    void testRemoveAuthorPhotoNotFound() {
        String authorNumber = "1L";
        long desiredVersion = 1L;

        when(authorRepository.findByAuthorNumber(authorNumber)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authorService.removeAuthorPhoto(authorNumber, desiredVersion));
    }
}
