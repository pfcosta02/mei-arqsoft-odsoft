package pt.psoft.g1.psoftg1.isbnmanagement.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OpenLibraryProviderTest {

    @InjectMocks
    @Autowired
    private OpenLibraryProvider provider;

    @Mock
    private RestTemplate restTemplate;

    @Value("${feature.enableSpecialCondition}")
    private boolean enableSpecialCondition;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(provider, "restTemplate", restTemplate);
    }

    @Test
    void deveRetornarIsbnQuandoCampoIsbnExiste() {
        Map<String, Object> doc = Map.of(
                "isbn", List.of("9780747546290")
        );
        Map<String, Object> responseMap = Map.of(
                "docs", List.of(doc)
        );
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");

        assertNotNull(resultado);
        assertEquals("9780747546290", resultado.getIsbn());
    }

    @Test
    void deveRetornarIsbnQuandoCampoIsbn10Existe() {
        Map<String, Object> doc = Map.of(
                "isbn", List.of("123456789X")
        );
        Map<String, Object> responseMap = Map.of(
                "docs", List.of(doc)
        );
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");

        assertNotNull(resultado);
        assertEquals("123456789X", resultado.getIsbn());
    }

    @Test
    void deveRetornarIsbnQuandoCampoIaTemPattern() {
        Map<String, Object> doc = Map.of(
                "ia", List.of("isbn_9780747546290")
        );
        Map<String, Object> responseMap = Map.of(
                "docs", List.of(doc)
        );
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");

        assertNotNull(resultado);
        assertEquals("9780747546290", resultado.getIsbn());
    }

    @Test
    void deveRetornarIsbn10QuandoCampoIaTemPattern() {
        Map<String, Object> doc = Map.of(
                "ia", List.of("isbn_0471958697")
        );
        Map<String, Object> responseMap = Map.of(
                "docs", List.of(doc)
        );
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");

        assertNotNull(resultado);
        assertEquals("0471958697", resultado.getIsbn());
    }

    @Test
    void deveRetornarIsbnQuandoCampoIaTemPatternComTexto() {
        Map<String, Object> doc = Map.of(
                "ia", List.of("isbn_9780747546290", "harrypotterhalfb0000rowl_j1n7")
        );
        Map<String, Object> responseMap = Map.of(
                "docs", List.of(doc)
        );
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");

        assertNotNull(resultado);
        assertEquals("9780747546290", resultado.getIsbn());
    }

    @Test
    void deveRetornarIsbnQuandoCampoIaTemPatternCom2Isbn() {
        Map<String, Object> doc = Map.of(
                "ia", List.of("isbn_9780786296651", "isbn_9780747546290")
        );
        Map<String, Object> responseMap = Map.of(
                "docs", List.of(doc)
        );
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");

        assertNotNull(resultado);
        assertEquals("9780786296651", resultado.getIsbn());
    }

    @Test
    void deveRetornarNullQuandoDocsEstaVazio() {
        Map<String, Object> responseMap = Map.of("docs", List.of());
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");

        assertNull(resultado);
    }

    @Test
    void deveRetornarNullQuandoEnableSpecialConditionNaoBate() {
        ReflectionTestUtils.setField(provider, "enableSpecialCondition", true);

        Map<String, Object> doc1 = Map.of("isbn", List.of("1111111111"));
        Map<String, Object> doc2 = Map.of("isbn", List.of("2222222222"));
        Map<String, Object> responseMap = Map.of("docs", List.of(doc1, doc2));
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        // enableSpecialCondition = true, docs.size() = 2 → condição não cumpre → retorna null
        Isbn resultado = provider.searchByTitle("teste");
        assertNull(resultado);
    }

    @Test
    void deveRetornarNullQuandoDocsNaoExiste() {
        Map<String, Object> responseMap = Map.of();
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");
        assertNull(resultado);
    }

    @Test
    void deveTratarExcecaoSemQuebrar() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Erro simulado"));

        Isbn resultado = provider.searchByTitle("teste");

        assertNull(resultado);
    }

    @Test
    void deveIgnorarDocComCamposInvalidos() {
        Map<String, Object> doc = Map.of(
                "isbn", "naoEhLista", // tipo errado
                "ia", 123 // tipo errado
        );
        Map<String, Object> responseMap = Map.of("docs", List.of(doc));
        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseMap);

        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        Isbn resultado = provider.searchByTitle("teste");
        assertNull(resultado);
    }
}