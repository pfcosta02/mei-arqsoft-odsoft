package pt.psoft.g1.psoftg1.bookmanagement.isbn.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.isbn.infrasctructure.IsbnProvider;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class OpenLibraryProvider implements IsbnProvider {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Isbn searchByTitle(String title) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://openlibrary.org/search.json")
                .queryParam("title", title)
                .queryParam("limit", 10)
                .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            var body = response.getBody();

            if (body != null && body.containsKey("docs")) {
                List<Map<String, Object>> docs = (List<Map<String, Object>>) body.get("docs");

                // Percorrer todos os documentos devolvidos pela pesquisa
                for (Map<String, Object> doc : docs) {

                    // Caso 1: campo "isbn" existe diretamente
                    if (doc.get("isbn") instanceof List<?> isbnList && !isbnList.isEmpty()) {
                        String isbnValue = (String) isbnList.get(0);
                        return new Isbn(isbnValue);
                    }

                    // Caso 2: procurar dentro do campo "ia"
                    if (doc.get("ia") instanceof List<?> iaList && !iaList.isEmpty()) {
                        for (Object iaObj : iaList) {
                            if (iaObj instanceof String iaString) {
                                // Procurar padrão isbn_ seguido de 10 a 13 dígitos
                                Matcher matcher = Pattern.compile("isbn_(\\d{10,13})").matcher(iaString);
                                if (matcher.find()) {
                                    String isbnValue = matcher.group(1);
                                    return new Isbn(isbnValue);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar Open Library API: " + e.getMessage());
        }

        // Se nada for encontrado
        return null;
    }
}
