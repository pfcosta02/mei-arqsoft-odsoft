package pt.psoft.g1.psoftg1.authormanagement.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BioTest {

    @Test
    void ensureBioMustNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new Bio(null));
    }

    @Test
    void ensureBioMustNotBeBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Bio(""));
    }

    @Test
    void ensureBioIsSet() {
        final var bio = new Bio("Some bio");
        assertEquals("Some bio", bio.toString());
    }

    @Test
    void ensureBioIsChanged() {
        final var bio = new Bio("Some bio");
        bio.setBio("Some other bio");
        assertEquals("Some other bio", bio.toString());
    }

    /* =========================================================== NOVOS TESTES =========================================================== */

    @Test
    void ensureBioIsSanitized() 
    {
        final var bio = new Bio("<script>alert('xss')</script>Clean bio");
        // Supondo que sanitizeHtml remove tags perigosas
        assertEquals("Clean bio", bio.getBio());
        assertEquals("Clean bio", bio.getValue());
    }

    @Test
    void ensureBioAtMaxLengthIsAccepted() 
    {
        String maxLengthBio = "a".repeat(Bio.BIO_MAX_LENGTH);
        Bio bio = new Bio(maxLengthBio);
        assertEquals(maxLengthBio, bio.getBio());
        assertEquals(maxLengthBio, bio.getValue());
    }

    @Test
    void ensureBioAboveMaxLengthIsRejected() 
    {
        String tooLongBio = "a".repeat(Bio.BIO_MAX_LENGTH + 1);
        assertThrows(IllegalArgumentException.class, () -> new Bio(tooLongBio));
    }
}
