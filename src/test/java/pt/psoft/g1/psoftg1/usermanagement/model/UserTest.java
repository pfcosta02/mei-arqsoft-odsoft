package pt.psoft.g1.psoftg1.usermanagement.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import pt.psoft.g1.psoftg1.shared.model.Name;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserTest {

    @Test
    void testNewUserWithNameAndPasswordEncoding() {
        // Arrange
        Name name = mock(Name.class);
        when(name.getName()).thenReturn("John Doe");
        // Act
        User user = User.newUser("john.doe", "password123", "John Doe");

        // Assert
        assertEquals("john.doe", user.getUsername());
        assertNotNull(user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$")); // BCrypt prefix
        assertTrue(user.isEnabled());
    }

    @Test
    void testNewUserWithRole() {
        // Act
        User user = User.newUser("jane.doe", "securePass", "Jane Doe", "ADMIN");

        // Assert
        assertEquals("jane.doe", user.getUsername());
        assertEquals(1, user.getAuthorities().size());
        assertTrue(user.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN")));
    }

    @Test
    void testSetPasswordWithAlreadyEncodedPassword() {
        // Arrange
        String encoded = new BCryptPasswordEncoder().encode("rawpass");
        User user = new User("user", encoded);

        // Act
        user.setPassword(encoded);

        // Assert
        assertEquals(encoded, user.getPassword());
    }

    @Test
    void testSetEnabledFalse() {
        User user = User.newUser("disabled.user", "pass", "Disabled User");
        user.setEnabled(false);

        assertFalse(user.isEnabled());
        assertFalse(user.isAccountNonExpired());
        assertFalse(user.isAccountNonLocked());
        assertFalse(user.isCredentialsNonExpired());
    }

    @Test
    void testAddAuthority() {
        User user = new User("user", "pass");
        Role role = new Role("USER");

        user.addAuthority(role);

        assertTrue(user.getAuthorities().contains(role));
    }
}