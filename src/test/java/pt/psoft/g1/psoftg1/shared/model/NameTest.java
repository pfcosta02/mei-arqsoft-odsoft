package pt.psoft.g1.psoftg1.shared.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NameTest 
{
    @Test
    void ensureNameMustNotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> new Name(null));
    }

    @Test
    void ensureNameMustNotBeBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Name(""));
    }

    @Test
    void ensureNameMustOnlyBeAlphanumeric() {
        assertThrows(IllegalArgumentException.class, () -> new Name("Ricardo!"));
    }


    /**
     * Text from <a href="https://www.lipsum.com/">Lorem Ipsum</a> generator.
     */
    @Test
    void ensureNameMustNotBeOversize() {
        assertThrows(IllegalArgumentException.class, () -> new Name("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse non laoreet sapien. Interdum et malesuada fames ac ante ipsum primis in faucibus. In non mattis quam efficitur."));
    }

    @Test
    void ensureNameIsSet() {
        final var name = new Name("Some name");
        assertEquals("Some name", name.toString());
    }

    @Test
    void ensureNameIsChanged() {
        final var name = new Name("Some name");
        name.setName("Some other name");
        assertEquals("Some other name", name.toString());
    }

    /* =========================================================== NOVOS TESTES =========================================================== */

    @Test
    void ensureNameWithNumbersIsRejected() 
    {
        assertThrows(IllegalArgumentException.class, () -> new Name("Ricardo123"));
    }

    @Test
    void ensureNameWithSpacesIsAccepted() 
    {
        assertDoesNotThrow(() -> new Name("Ricardo Manuel"));
    }


}
