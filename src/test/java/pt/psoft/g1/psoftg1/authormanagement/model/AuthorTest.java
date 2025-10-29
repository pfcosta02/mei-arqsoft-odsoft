package pt.psoft.g1.psoftg1.authormanagement.model;

import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;
import pt.psoft.g1.psoftg1.authormanagement.services.UpdateAuthorRequest;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.Photo;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthorTest {
    private final String validName = "Joao Alberto";
    private final String validBio = "O João Alberto nasceu em Chaves e foi pedreiro a maior parte da sua vida.";

    @BeforeEach
    void setUp() {
    }
    @Test
    void ensureNameNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Author(null,validBio, null));
    }

    @Test
    void ensureBioNotNull(){
        assertThrows(IllegalArgumentException.class, () -> new Author(validName,null, null));
    }

    @Test
    void whenVersionIsStaleItIsNotPossibleToPatch() {
        // Arrange
        final var subject = new Author(validName,validBio, null);

        UpdateAuthorRequest request = mock(UpdateAuthorRequest.class);

        // Act + Assert
        assertThrows(StaleObjectStateException.class, () -> subject.applyPatch(999, request));
    }

    @Test
    void testCreateAuthorWithoutPhoto() {
        // Arrange + Act
        Author author = new Author(validName, validBio, null);

        // Assert
        assertNotNull(author);
        assertNull(author.getPhoto());
    }

    @Test
    void testCreateAuthorRequestWithPhoto() 
    {
        // Arrange
        CreateAuthorRequest request = mock(CreateAuthorRequest.class);
        when(request.getName()).thenReturn(validName);
        when(request.getBio()).thenReturn(validBio);
        when(request.getPhotoURI()).thenReturn("photoTest.jpg");

        // Act
        Author author = new Author(request.getName(), request.getBio(), "photoTest.jpg");

        // Assert
        assertNotNull(author);
        assertEquals(request.getPhotoURI(), author.getPhoto().getPhotoFile());
    }

    @Test
    void testCreateAuthorRequestWithoutPhoto() 
    {
        // Arrange
        CreateAuthorRequest request = mock(CreateAuthorRequest.class);
        when(request.getName()).thenReturn(validName);
        when(request.getBio()).thenReturn(validBio);

        // Act
        Author author = new Author(request.getName(), request.getBio(), null);

        // Assert
        assertNotNull(author);
        assertNull(author.getPhoto());
    }

    @Test
    void ensurePhotoCanBeNull_AkaOptional() {
        Author author = new Author(validName, validBio, null);
        assertNull(author.getPhoto());
    }

    @Test
    void ensureValidPhoto() 
    {
        // Arrange
        String url = "photoTest.jpg";
        Photo photo = mock(Photo.class);
        when(photo.getPhotoFile()).thenReturn(url);

        // Act
        Author author = new Author(validName, validBio, url);
    
        // Assert
        assertNotNull(author.getPhoto().getPhotoFile());
        assertEquals(url, photo.getPhotoFile());
    }

    /* =========================================================== NOVOS TESTES =========================================================== */
    @Test
    void ensureNameNotNullException(){
        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Author(null,validBio, null));
        
        // Assert
        assertEquals("Name cannot be null", exception.getMessage());
    }

    @Test
    void ensureBioNotNullException(){
        // Act
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Author(validName,null, null));

        // Assert
        assertEquals("Bio cannot be null", exception.getMessage());
    }

    @Test
    void ensureAuthorReturnAuthorNumberAndVersionCorrectly()
    {
        // Arrange
        String authorNumber = "1L";

        pt.psoft.g1.psoftg1.shared.model.Name name = mock(pt.psoft.g1.psoftg1.shared.model.Name.class);
        Bio bio = mock(Bio.class);
        Photo photo = mock(Photo.class);
        Author author = new Author(name, bio, photo);

        // Act
        author.setAuthorNumber(authorNumber);

        // Assert
        assertEquals(authorNumber, author.getAuthorNumber());
        assertEquals(0L, author.getVersion());
    }

    @Test
    void ensureAuthorUsesProvidedNameAndBio() 
    {
        // Arrange
        pt.psoft.g1.psoftg1.shared.model.Name name = mock(pt.psoft.g1.psoftg1.shared.model.Name.class);
        Bio bio = mock(Bio.class);
        Photo photo = mock(Photo.class);

        // Act
        Author author = new Author(name, bio, photo);

        // Assert
        assertEquals(name, author.getName());
        assertEquals(bio, author.getBio());
        assertEquals(photo, author.getPhoto());
    }

    @Test
    void ensureApplyPatchReplacesFields() {
        // Arrange
        Name mockName = mock(Name.class);
        Bio mockBio = mock(Bio.class);
        Author author = new Author(mockName, mockBio, null);
        UpdateAuthorRequest request = mock(UpdateAuthorRequest.class);
        when(request.getName()).thenReturn("Novo Nome");
        when(request.getBio()).thenReturn("Nova Bio");
        when(request.getPhotoURI()).thenReturn("novaFoto.jpg");

        MultipartFile mockFile = mock(MultipartFile.class);
        when(request.getPhoto()).thenReturn(mockFile);

        // Act
        author.applyPatch(0L, request);

        // Assert
        assertNotSame(mockName, author.getName());
        assertNotSame(mockBio, author.getBio());
        assertNotNull(author.getPhoto());
    }

    @Test
    void ensureApplyPatchDoesNotUpdateWhenFieldsAreNull() 
    {
        // Arrange
        Name mockName = mock(Name.class);
        Bio mockBio = mock(Bio.class);
        Photo mockPhoto = mock(Photo.class);
        Author author = new Author(mockName, mockBio, mockPhoto);
        UpdateAuthorRequest request = mock(UpdateAuthorRequest.class);
        when(request.getName()).thenReturn(null);
        when(request.getBio()).thenReturn(null);
        when(request.getPhoto()).thenReturn(null);

        // Act
        author.applyPatch(0L, request);

        // Assert
        assertEquals(mockName, author.getName());
        assertEquals(mockBio, author.getBio());
        assertEquals(mockPhoto, author.getPhoto());
    }

    @Test
    void ensureRemovePhotoSucceedsWithCorrectVersion() 
    {
        // Arrange
        Author author = new Author(validName, validBio, "foto.jpg");
        Long version = author.getVersion();

        // Act
        author.removePhoto(version);

        // Assert
        assertNull(author.getPhoto());
    }

    @Test
    void ensureRemovePhotoFailsWithIncorrectVersion() 
    {
        // Arrange
        Author author = new Author(validName, validBio, "foto.jpg");

        // Act + Assert
        assertThrows(pt.psoft.g1.psoftg1.exceptions.ConflictException.class,
            () -> author.removePhoto(999L));
    }

    @Test
    void ensureGettersReturnCorrectValues() 
    {
        // Arrange
        Name mockName = mock(Name.class);
        Bio mockBio = mock(Bio.class);
        Photo mockPhoto = mock(Photo.class);

        Author author = new Author(mockName, mockBio, mockPhoto);

        // Assert
        assertEquals(mockName, author.getName());
        assertEquals(mockBio, author.getBio());
        assertEquals(mockPhoto, author.getPhoto());
    }


}

