package pt.psoft.g1.psoftg1.isbnmanagement.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class GoogleBooksProviderTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    @Autowired
    private GoogleBooksProvider provider;

    @Value("${isbn.google.api-key}")
    private String apiKey;

    @Value("${feature.enableSpecialCondition}")
    private boolean enableSpecialCondition;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // injeta o RestTemplate mockado manualmente, já que o original é final
        ReflectionTestUtils.setField(provider, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(provider, "apiKey", apiKey);
    }

    @Test
    void deveRetornarIsbn13QuandoPresente() {
        Map<String, Object> responseBody = Map.of(
                "items", List.of(Map.of(
                        "volumeInfo", Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_13", "identifier", "9788565050319")
                                )
                        )
                ))
        );

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(responseEntity);

        Isbn result = provider.searchByTitle("teste");

        assertNotNull(result);
        assertEquals("9788565050319", result.getIsbn());
    }

    @Test
    void deveRetornarIsbn10QuandoIsbn13NaoExiste() {
        Map<String, Object> responseBody = Map.of(
                "items", List.of(Map.of(
                        "volumeInfo", Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_10", "identifier", "123456789X")
                                )
                        )
                ))
        );

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(responseEntity);

        Isbn result = provider.searchByTitle("Another Book");

        assertNotNull(result);
        assertEquals("123456789X", result.getIsbn());
    }

    @Test
    void deveRetornarIsbn13QuandoExisteIsbn10e13() {
        Map<String, Object> responseBody = Map.of(
                "items", List.of(Map.of(
                        "volumeInfo", Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_10", "identifier", "1010101010"),
                                        Map.of("type", "ISBN_13", "identifier", "9788565050319")
                                )
                        )
                ))
        );

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(responseEntity);

        Isbn result = provider.searchByTitle("Another Book");

        assertNotNull(result);
        assertEquals("9788565050319", result.getIsbn());
    }

    @Test
    void naoExisteindustryIdentifiers() {
        Map<String, Object> responseBody = Map.of(
                "items", List.of(Map.of(
                        "volumeInfo", Map.of()
                ))
        );

        ResponseEntity<Map> responseEntity = ResponseEntity.ok(responseBody);

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(responseEntity);

        Isbn result = provider.searchByTitle("Another Book");

        assertNull(result);
    }

    @Test
    void deveRetornarNullQuandoNaoHaItems() {
        Map<String, Object> responseBody = Map.of(); // sem items
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        Isbn result = provider.searchByTitle("Missing Book");

        assertNull(result);
    }

    @Test
    void deveRetornarNullQuandoOcorreErroNaChamada() {
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("Erro HTTP"));

        Isbn result = provider.searchByTitle("Bad Book");

        assertNull(result);
    }

    @Test
    void deveObedecerEnableSpecialCondition() {
        // ativa o comportamento especial
        ReflectionTestUtils.setField(provider, "enableSpecialCondition", true);

        Map<String, Object> responseBody = Map.of(
                "items", List.of(
                        Map.of("volumeInfo", Map.of(
                                "industryIdentifiers", List.of(
                                        Map.of("type", "ISBN_13", "identifier", "9999999999999")
                                )
                        )),
                        Map.of("volumeInfo", Map.of()) // dois itens (size=2)
                )
        );

        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // Como enableSpecialCondition=true e items.size()!=1, deve retornar null
        Isbn result = provider.searchByTitle("Special Case");

        assertNull(result);
    }
}