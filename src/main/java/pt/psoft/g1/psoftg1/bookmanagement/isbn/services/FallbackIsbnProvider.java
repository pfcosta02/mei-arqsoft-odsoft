package pt.psoft.g1.psoftg1.bookmanagement.isbn.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.bookmanagement.isbn.impl.GoogleBooksProvider;
import pt.psoft.g1.psoftg1.bookmanagement.isbn.impl.OpenLibraryProvider;
import pt.psoft.g1.psoftg1.bookmanagement.isbn.infrasctructure.IsbnProvider;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;

@Component
@RequiredArgsConstructor
public class FallbackIsbnProvider implements IsbnProvider {

    private final OpenLibraryProvider openLibraryProvider;
    private final GoogleBooksProvider googleBooksProvider;

    @Override
    public Isbn searchByTitle(String title) {
        try {
            Isbn result = openLibraryProvider.searchByTitle(title);
            if (result != null && result.getIsbn() != null) {
                return result;
            }
        } catch (Exception e) {
            System.out.println("[FallbackIsbnProvider] OpenLibrary falhou: " + e.getMessage());
        }

        // fallback para Google Books
        try {
            return googleBooksProvider.searchByTitle(title);
        } catch (Exception e) {
            System.out.println("[FallbackIsbnProvider] GoogleBooks falhou: " + e.getMessage());
            throw new RuntimeException("Falha em ambos os provedores.");
        }
    }


}
