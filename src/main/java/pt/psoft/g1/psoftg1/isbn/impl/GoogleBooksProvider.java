package pt.psoft.g1.psoftg1.isbn.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.isbn.infrasctructure.IsbnProvider;

import java.util.List;
import java.util.Map;

@Component
public class GoogleBooksProvider implements IsbnProvider {

    @Value("${isbn.google.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Isbn searchByTitle(String title) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://www.googleapis.com/books/v1/volumes")
                .queryParam("q", "intitle:" + title)
                .queryParam("key", apiKey)
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            var body = response.getBody();

            if (body != null && body.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

                for (Map<String, Object> item : items) {
                    Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");

                    // Buscar o ISBN_13 (preferencialmente)
                    if (volumeInfo.get("industryIdentifiers") instanceof List<?> ids) {
                        for (Object idObj : ids) {
                            Map<String, String> idMap = (Map<String, String>) idObj;
                            if ("ISBN_13".equals(idMap.get("type"))) {
                                return new Isbn(idMap.get("identifier"));
                            }
                        }
                    }

                    // Se não houver ISBN_13, tenta pegar o ISBN_10
                    if (volumeInfo.get("industryIdentifiers") instanceof List<?> idsFallback) {
                        for (Object idObj : idsFallback) {
                            Map<String, String> idMap = (Map<String, String>) idObj;
                            if ("ISBN_10".equals(idMap.get("type"))) {
                                return new Isbn(idMap.get("identifier"));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar Google Books API: " + e.getMessage());
        }

        return null; // nenhum ISBN encontrado
    }
}
