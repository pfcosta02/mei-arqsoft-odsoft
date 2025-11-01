package pt.psoft.g1.psoftg1.isbnmanagement.impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FallbackIsbnProviderTest {

    private OpenLibraryProvider openLibraryProvider;
    private GoogleBooksProvider googleBooksProvider;
    private FallbackIsbnProvider fallbackProvider;

    @BeforeEach
    void setup() {
        openLibraryProvider = mock(OpenLibraryProvider.class);
        googleBooksProvider = mock(GoogleBooksProvider.class);
        fallbackProvider = new FallbackIsbnProvider(openLibraryProvider, googleBooksProvider);
    }

    // ------------------------------------------------------------------------
    // Cenário 1: OpenLibrary tem sucesso → GoogleBooks não deve ser chamado
    // ------------------------------------------------------------------------
    @Test
    void deveUsarOpenLibraryQuandoRetornaResultadoValido() {
        Isbn isbn = new Isbn("9789720706386");
        when(openLibraryProvider.searchByTitle("Test Book")).thenReturn(isbn);

        Isbn resultado = fallbackProvider.searchByTitle("Test Book");

        assertNotNull(resultado);
        assertEquals("9789720706386", resultado.getIsbn());

        verify(openLibraryProvider, times(1)).searchByTitle("Test Book");
        verify(googleBooksProvider, never()).searchByTitle(anyString());
    }

    // ------------------------------------------------------------------------
    // Cenário 2: OpenLibrary retorna null → GoogleBooks é chamado
    // ------------------------------------------------------------------------
    @Test
    void deveUsarGoogleBooksQuandoOpenLibraryRetornaNull() {
        when(openLibraryProvider.searchByTitle("Test Book")).thenReturn(null);
        when(googleBooksProvider.searchByTitle("Test Book")).thenReturn(new Isbn("9789720706386"));

        Isbn resultado = fallbackProvider.searchByTitle("Test Book");

        assertNotNull(resultado);
        assertEquals("9789720706386", resultado.getIsbn());

        verify(openLibraryProvider).searchByTitle("Test Book");
        verify(googleBooksProvider).searchByTitle("Test Book");
    }

    // ------------------------------------------------------------------------
    // Cenário 3: OpenLibrary lança exceção → GoogleBooks é usado como fallback
    // ------------------------------------------------------------------------
    @Test
    void deveChamarGoogleBooksQuandoOpenLibraryLancaExcecao() {
        when(openLibraryProvider.searchByTitle("Test Book")).thenThrow(new RuntimeException("Erro na OpenLibrary"));
        when(googleBooksProvider.searchByTitle("Test Book")).thenReturn(new Isbn("9789720706386"));

        Isbn resultado = fallbackProvider.searchByTitle("Test Book");

        assertNotNull(resultado);
        assertEquals("9789720706386", resultado.getIsbn());

        verify(openLibraryProvider).searchByTitle("Test Book");
        verify(googleBooksProvider).searchByTitle("Test Book");
    }

    // ------------------------------------------------------------------------
    // Cenário 4: Ambos falham → lança RuntimeException
    // ------------------------------------------------------------------------
    @Test
    void deveLancarExcecaoQuandoAmbosOsProvedoresFalham() {
        when(openLibraryProvider.searchByTitle("Test Book")).thenThrow(new RuntimeException("Erro OL"));
        when(googleBooksProvider.searchByTitle("Test Book")).thenThrow(new RuntimeException("Erro GB"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> fallbackProvider.searchByTitle("Test Book"));

        assertEquals("Falha em ambos os provedores.", ex.getMessage());

        verify(openLibraryProvider).searchByTitle("Test Book");
        verify(googleBooksProvider).searchByTitle("Test Book");
    }
}
