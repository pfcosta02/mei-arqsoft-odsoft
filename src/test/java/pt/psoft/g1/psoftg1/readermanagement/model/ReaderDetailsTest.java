package pt.psoft.g1.psoftg1.readermanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.services.UpdateReaderRequest;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReaderDetailsTest {
    private Reader mockReader;

    @BeforeEach
    void setUp() 
    {
        mockReader = mock(Reader.class);
    }

    @Test
    void ensureValidReaderDetailsAreCreated() 
    {
        // Act + Assert
        assertDoesNotThrow(() -> new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false,null, null));
    }

    @Test
    void ensureExceptionIsThrownForNullReader() 
    {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new ReaderDetails(123, null, "2010-01-01", "912345678", true, false, false,null,null));
    }

    @Test
    void ensureExceptionIsThrownForNullPhoneNumber() 
    {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new ReaderDetails(123, mockReader, "2010-01-01", null, true, false, false,null,null));
    }

    @Test
    void ensureExceptionIsThrownForNoGdprConsent() 
    {
        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> new ReaderDetails(123, mockReader, "2010-01-01", "912345678", false, false, false,null,null));
    }

    @Test
    void ensureGdprConsentIsTrue() 
    {
        // Act
        ReaderDetails readerDetails = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false,null,null);
        
        // Assert
        assertTrue(readerDetails.isGdprConsent());
    }

    @Test
    void ensurePhotoCanBeNull_AkaOptional() 
    {
        // Act
        ReaderDetails readerDetails = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false,null,null);
        
        // Assert
        assertNull(readerDetails.getPhoto());
    }

    @Test
    void ensureValidPhoto() 
    {
        // Arrange
        String photo = "readerPhotoTest.jpg";
        Photo mockPhoto = mock(Photo.class);
        when(mockPhoto.getPhotoFile()).thenReturn(photo);

        // Act
        ReaderDetails readerDetails = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false,mockPhoto.getPhotoFile(),null);
        
        // Assert
        //This is here to force the test to fail if the photo is null
        assertNotNull(readerDetails.getPhoto());
    }

    @Test
    void ensureInterestListCanBeNullOrEmptyList_AkaOptional() 
    {
        ReaderDetails readerDetailsNullInterestList = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false,"readerPhotoTest.jpg",null);
        assertNull(readerDetailsNullInterestList.getInterestList());

        ReaderDetails readerDetailsInterestListEmpty = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false,"readerPhotoTest.jpg",new ArrayList<>());
        assertEquals(0, readerDetailsInterestListEmpty.getInterestList().size());
    }

    @Test
    void ensureInterestListCanTakeAnyValidGenre() 
    {
        Genre mockGenre1 = mock(Genre.class);
        Genre mockGenre2 = mock(Genre.class);
        List<Genre> genreList = List.of(mockGenre1, mockGenre2);

        ReaderDetails readerDetails = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false,"readerPhotoTest.jpg",genreList);
        assertEquals(2, readerDetails.getInterestList().size());
    }

    /* =========================================================== NOVOS TESTES =========================================================== */

    @Test
    void ensureConflictExceptionIsThrownOnVersionMismatchInPatch() 
    {
        // Arrange
        ReaderDetails readerDetails = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false, null, null);
        readerDetails.setVersion(1L);
        UpdateReaderRequest mockRequest = mock(UpdateReaderRequest.class);
        
        // Act + Assert
        assertThrows(ConflictException.class, () -> readerDetails.applyPatch(2L, mockRequest, null, null));
    }

    @Test
    void ensurePatchUpdatesFieldsCorrectly() 
    {
        // Arrange
        BirthDate mockBirthDate = mock(BirthDate.class);
        PhoneNumber mockPhoneNumber = mock(PhoneNumber.class);
        ReaderNumber mockReaderNumber = mock(ReaderNumber.class);
        ReaderDetails readerDetails = new ReaderDetails(
            mockReaderNumber,
            mockReader,
            mockBirthDate,
            mockPhoneNumber,
            true, false, false,
            null, null
        );
        readerDetails.setVersion(1L);

        UpdateReaderRequest mockRequest = mock(UpdateReaderRequest.class);
        when(mockRequest.getUsername()).thenReturn("newUsername");
        when(mockRequest.getPassword()).thenReturn("newPassword");
        when(mockRequest.getFullName()).thenReturn("New Name");
        when(mockRequest.getBirthDate()).thenReturn("2000-01-01");
        when(mockRequest.getPhoneNumber()).thenReturn("911111111");
        when(mockRequest.getMarketing()).thenReturn(true);
        when(mockRequest.getThirdParty()).thenReturn(true);

        // Act
        // Para ter a certeza
        assertEquals(mockBirthDate, readerDetails.getBirthDate());
        readerDetails.applyPatch(1L, mockRequest, null, null);

        // Assert
        verify(mockReader).setUsername("newUsername");
        verify(mockReader).setPassword("newPassword");
        verify(mockReader).setName("New Name");
        assertNotEquals(mockBirthDate, readerDetails.getBirthDate());
        assertTrue(readerDetails.isMarketingConsent());
        assertTrue(readerDetails.isThirdPartySharingConsent());
    }

    @Test
    void ensureRemovePhotoThrowsConflictExceptionOnVersionMismatch() 
    {
        // Arrange
        ReaderDetails readerDetails = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false, "readerPhotoTest.jpg", null);
        readerDetails.setVersion(1L);

        // Act + Assert
        assertThrows(ConflictException.class, () -> readerDetails.removePhoto(2L));
    }

    @Test
    void ensureRemovePhotoSetsPhotoToNull() 
    {
        // Arrange
        ReaderDetails readerDetails = new ReaderDetails(123, mockReader, "2010-01-01", "912345678", true, false, false, "readerPhotoTest.jpg", null);
        readerDetails.setVersion(1L);

        // Act
        readerDetails.removePhoto(1L);

        // Assert
        assertNull(readerDetails.getPhoto());
    }



}
