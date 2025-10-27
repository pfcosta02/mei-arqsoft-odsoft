package pt.psoft.g1.psoftg1.shared.model;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class EntityWithPhotoTest {
    // Como EntityWithPhoto é uma classe abstrata, vamos criar uma implementação mínima para testes
    private static class  EntityWithPhotoImpl extends EntityWithPhoto { }

    @Test
    void ensureSetPhotoWithValidPathSetsPhoto() 
    {
        // Arrange
        EntityWithPhoto entity = new EntityWithPhotoImpl();
        String validPath = "valid/photo.jpg";

        // Act
        entity.setPhoto(validPath);

        // Assert
        assertNotNull(entity.getPhoto()); // sem entrar na lógica de Photo
    }


    @Test
    void ensureSetPhotoWithNullSetsPhotoToNull() 
    {
        // Arrange
        EntityWithPhoto entity = new EntityWithPhotoImpl();

        // Act
        entity.setPhoto(null);

        // Assert
        assertNull(entity.getPhoto());
    }

    @Test
    void ensureSetPhotoWithInvalidPathSetsPhotoToNull() 
    {
        // Arrange
        EntityWithPhoto entity = new EntityWithPhotoImpl();
        // Aqui precisei de alterar para '\0' porque o comportamento do Path.of muda do docker para o windows.
        // Antes tinha '??invalid//path' e passava no Docker
        String invalidPath = "\0";

        // Act
        entity.setPhoto(invalidPath);

        // Assert
        assertNull(entity.getPhoto());
    }

    @Test
    void ensureSetPhotoInternalWithMockPhotoSetsItCorrectly() 
    {
        // Arrange
        EntityWithPhoto entity = new EntityWithPhotoImpl();
        Photo photoMock = mock(Photo.class);

        // Act
        entity.setPhotoInternal(photoMock);

        // Assert
        assertEquals(photoMock, entity.getPhoto());
    }

    @Test
    void ensureSetPhotoInternalWithNullKeepsPhotoNull() 
    {
        // Arrange
        EntityWithPhoto entity = new EntityWithPhotoImpl();

        // Act
        entity.setPhotoInternal((Photo) null);

        // Assert
        assertNull(entity.getPhoto());
    }

    
}
