package pt.psoft.g1.psoftg1.usermanagement.model;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class LibrarianTest {

    @Test
    void testLibrarianConstructorWithoutRole() {
        // Act
        Librarian librarian = new Librarian("lib.user", "libPass");

        // Assert
        assertEquals("lib.user", librarian.getUsername());
        assertNotNull(librarian.getPassword());
        assertTrue(librarian.getPassword().startsWith("$2a$")); // BCrypt encoded
        assertTrue(librarian.getAuthorities().isEmpty()); // No role added by constructor
    }

    @Test
    void testNewLibrarianFactoryMethodAddsRoleAndName() {
        // Act
        Librarian librarian = Librarian.newLibrarian("lib.user", "libPass", "Lib Name");

        // Assert
        assertEquals("lib.user", librarian.getUsername());
        assertEquals(1, librarian.getAuthorities().size());
        assertTrue(librarian.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals(Role.LIBRARIAN)));
    }

    @Test
    void testLibrarianIsEnabledByDefault() {
        Librarian librarian = new Librarian("lib.user", "libPass");

        assertTrue(librarian.isEnabled());
        assertTrue(librarian.isAccountNonExpired());
        assertTrue(librarian.isAccountNonLocked());
        assertTrue(librarian.isCredentialsNonExpired());
    }
}