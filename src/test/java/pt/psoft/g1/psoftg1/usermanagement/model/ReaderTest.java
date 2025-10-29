package pt.psoft.g1.psoftg1.usermanagement.model;

import org.junit.jupiter.api.Test;
import pt.psoft.g1.psoftg1.shared.model.Name;

import static org.junit.jupiter.api.Assertions.*;

class ReaderTest {

    @Test
    void testReaderConstructorAddsReaderRole() {
        // Act
        Reader reader = new Reader("reader.user", "readerPass");

        // Assert
        assertEquals("reader.user", reader.getUsername());
        assertNotNull(reader.getPassword());
        assertTrue(reader.getPassword().startsWith("$2a$")); // BCrypt encoded
        assertTrue(reader.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(Role.READER)));
    }

    @Test
    void testNewReaderFactoryMethod() {
        // Act
        Reader reader = Reader.newReader("reader.user", "readerPass", "Reader Name");

        // Assert
        assertEquals("reader.user", reader.getUsername());
        assertTrue(reader.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(Role.READER)));
    }

    @Test
    void testReaderIsEnabledByDefault() {
        Reader reader = new Reader("reader.user", "readerPass");

        assertTrue(reader.isEnabled());
        assertTrue(reader.isAccountNonExpired());
        assertTrue(reader.isAccountNonLocked());
        assertTrue(reader.isCredentialsNonExpired());
    }
}
