package pt.psoft.g1.psoftg1.bookmanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.services.UpdateBookRequest;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.shared.model.Photo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookTest 
{
    private final String validIsbn = "9782826012092";
    private final String validTitle = "Encantos de contar";

    @BeforeEach
    void setUp()
    {
    }

    // Comentados porque a classe Book possui instâncias das classes (Isbn, Title, etc.),
    // e cada uma dessas classes já possui os seus próprios testes unitários.
    // Não é necessário duplicar a validação das regras internas aqui.
    // @Test
    // void ensureIsbnNotNull(){
    //     authors.add(validAuthor1);
    //     assertThrows(IllegalArgumentException.class, () -> new Book(null, validTitle, null, validGenre, authors, null));
    // }

    // @Test
    // void ensureTitleNotNull(){
    //     authors.add(validAuthor1);
    //     assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, null, null, validGenre, authors, null));
    // }

    // @Test
    // void ensureGenreNotNull(){
    //     authors.add(validAuthor1);
    //     assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, null,null, authors, null));
    // }

    @Test
    void ensureAuthorsNotNull()
    {
        // Arrange
        Genre mockGenre = mock(Genre.class);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, null, mockGenre, null, null));
    }

    @Test
    void ensureAuthorsNotEmpty()
    {
        // Arrange
        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of();

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, null, mockGenre, authors, null));
    }

    @Test
    void ensureBookCreatedWithMultipleAuthors() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);
        Author mockAuthor2 = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor, mockAuthor2);
        
        // Act + Assert
        assertDoesNotThrow(() -> new Book(validIsbn, validTitle, null, mockGenre, authors, null));
    }

    /* =========================================================== NOVOS TESTES =========================================================== */

    @Test
    void ensureAuthorsNotNullException()
    {
        // Arrange
        Genre mockGenre = mock(Genre.class);
    
        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Book(validIsbn, validTitle, null, mockGenre, null, null));
    
        // Assert
        assertEquals("Authors cannot be empty", exception.getMessage());
    }

    @Test
    void whenAuthorsListHasOneAuthor_thenBookCreatedSuccessfully() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor);

        // Act
        Book book = new Book(validIsbn, validTitle, null, mockGenre, authors, null);

        // Assert
        assertNotNull(book);
        assertEquals(1, book.getAuthors().size());
        assertTrue(book.getAuthors().contains(mockAuthor));
    }

    @Test
    void whenAuthorsListHasTwoAuthors_thenBookCreatedSuccessfully() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);
        Author mockAuthor2 = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor, mockAuthor2);

        // Act
        Book book = new Book(validIsbn, validTitle, null, mockGenre, authors, null);

        // Assert
        assertNotNull(book);
        assertEquals(2, book.getAuthors().size());
        assertTrue(book.getAuthors().contains(mockAuthor));
        assertTrue(book.getAuthors().contains(mockAuthor2));
    }

    @Test
    void ensureBookStoresEverythingCorrectly() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor);

        String description = "Uma história mágica.";
        String photoURI = "bookPhotoTest.jpg";

        Description mockDescription = mock(Description.class);


        Title mockTitle = mock(Title.class);
        when(mockTitle.getTitle()).thenReturn(validTitle);

        Isbn mockIsbn = mock(Isbn.class);
        when(mockIsbn.getIsbn()).thenReturn(validIsbn);

        Photo mockPhoto = mock(Photo.class);
        when(mockPhoto.getPhotoFile()).thenReturn(photoURI);

        // Act
        Book book = new Book(validIsbn, validTitle, description, mockGenre, authors, photoURI);
        book.setDescription(mockDescription);
        
        // Assert
        assertEquals(validIsbn, book.getIsbn().getIsbn());
        assertEquals(validTitle, book.getTitle().getTitle());
        assertEquals(mockDescription, book.getDescription());
        assertTrue(book.getAuthors().contains(mockAuthor));
        assertEquals(photoURI, book.getPhoto().getPhotoFile());
    }

    @Test
    void applyPatchUpdatesOnlyTitle() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor);

        String description = "Descrição Original";

        Title mockTitle = mock(Title.class);

        Description mockDescription = mock(Description.class);

        Book book = new Book("9782826012092", "Título Original", description, mockGenre, authors, null);
        book.setDescription(mockDescription);
        book.setTitle(mockTitle);
        UpdateBookRequest mockRequest = mock(UpdateBookRequest.class);

        when(mockRequest.getTitle()).thenReturn("Novo Título");
        when(mockRequest.getDescription()).thenReturn(null);
        when(mockRequest.getGenreObj()).thenReturn(null);
        when(mockRequest.getAuthorObjList()).thenReturn(null);
        when(mockRequest.getPhotoURI()).thenReturn(null);

        // Act
        book.applyPatch(0L, mockRequest);

        // Assert
        assertNotEquals(mockTitle, book.getTitle());
        assertEquals(mockDescription, book.getDescription());
        assertEquals(mockGenre, book.getGenre());
        assertEquals(authors, book.getAuthors());
    }

    @Test
    void applyPatchUpdatesAllFields() 
    {
        // Arrange
        String description = "Descrição Original";

        // PRE
        Author mockAuthor = mock(Author.class);
        Genre mockGenre = mock(Genre.class);
        List<Author> authors = List.of(mockAuthor);

        Title mockTitle = mock(Title.class);
        Description mockDescription = mock(Description.class);
        Photo mockPhoto = mock(Photo.class);

        Book book = new Book("9782826012092", "Título Original", description, mockGenre, authors, null);
        book.setPhotoByClass(mockPhoto);
        // POS
        Genre newGenre = mock(Genre.class);
        Author newAuthor = mock(Author.class);

        UpdateBookRequest mockRequest = mock(UpdateBookRequest.class);
        when(mockRequest.getTitle()).thenReturn("Novo Título");
        when(mockRequest.getDescription()).thenReturn("Nova Descrição");
        when(mockRequest.getGenreObj()).thenReturn(newGenre);
        when(mockRequest.getAuthorObjList()).thenReturn(List.of(newAuthor));
        when(mockRequest.getPhotoURI()).thenReturn("caminho/para/foto.jpg");

        // Act
        book.applyPatch(0L, mockRequest);

        // Assert
        // Primeiro verificar que os valores antigos mudaram
        assertNotEquals(mockTitle, book.getTitle());
        assertNotEquals(mockDescription, book.getDescription());
        assertNotEquals(mockGenre, book.getGenre());
        assertFalse(book.getAuthors().contains(mockAuthor));
        assertNotEquals(mockPhoto, book.getPhoto());
    }

    @Test
    void removePhotoWithCorrectVersionRemovesPhoto() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor);

        Book book = new Book(validIsbn, validTitle, null, mockGenre, authors, "bookPhotoTest.jpg");

        assertNotNull(book.getPhoto());

        // Act
        book.removePhoto(0L);

        // Assert
        assertNull(book.getPhoto());
    }

    @Test
    void removePhotoWithWrongVersionThrowsConflictException()
     {
        // Arrange
        Author mockAuthor = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor);

        Book book = new Book(validIsbn, validTitle, null, mockGenre, authors, "bookPhotoTest.jpg");

        // Act + Assert
        assertThrows(pt.psoft.g1.psoftg1.exceptions.ConflictException.class, () -> book.removePhoto(1L));
    }

    @Test
    void applyPatchWithNullAuthorsDoesNotChangeAuthors() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor);

        Book book = new Book(validIsbn, validTitle, null, mockGenre, authors, null);

        UpdateBookRequest mockRequest = mock(UpdateBookRequest.class);
        when(mockRequest.getAuthorObjList()).thenReturn(null);

        //  Act
        book.applyPatch(0L, mockRequest);

        // Assert
        assertEquals(1, book.getAuthors().size());
        assertTrue(book.getAuthors().contains(mockAuthor));
    }

    @Test
    void applyPatchWithEmptyAuthorsThrowsException() 
    {
        // Arrange
        Author mockAuthor = mock(Author.class);

        Genre mockGenre = mock(Genre.class);

        List<Author> authors = List.of(mockAuthor);

        Book book = new Book(validIsbn, validTitle, null, mockGenre, authors, null);

        UpdateBookRequest mockRequest = mock(UpdateBookRequest.class);
        when(mockRequest.getAuthorObjList()).thenReturn(new ArrayList<>());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> book.applyPatch(0L, mockRequest));
    }

    @Test
    void ensureGenreIsSetCorrectlyInConstructor() 
    {
        // Arrange
        Genre mockGenre = mock(Genre.class);
        Author mockAuthor = mock(Author.class);
        List<Author> authors = List.of(mockAuthor);

        // Act
        Book book = new Book(validIsbn, validTitle, null, mockGenre, authors, null);

        // Assert
        assertNotNull(book.getGenre());
        assertEquals(mockGenre, book.getGenre());
    }

}