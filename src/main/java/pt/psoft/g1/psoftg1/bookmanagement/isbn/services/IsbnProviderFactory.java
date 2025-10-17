package pt.psoft.g1.psoftg1.bookmanagement.isbn.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.bookmanagement.isbn.impl.GoogleBooksProvider;
import pt.psoft.g1.psoftg1.bookmanagement.isbn.impl.OpenLibraryProvider;
import pt.psoft.g1.psoftg1.bookmanagement.isbn.infrasctructure.IsbnProvider;

@Component
public class IsbnProviderFactory {

    private final GoogleBooksProvider googleBooksProvider;

    private final OpenLibraryProvider openLibraryProvider;

    @Value("${isbn.provider}")
    private String activeProvider;

    @Autowired
    public IsbnProviderFactory(GoogleBooksProvider googleBooksProvider, OpenLibraryProvider openLibraryProvider) {
        this.googleBooksProvider = googleBooksProvider;
        this.openLibraryProvider = openLibraryProvider;
    }

    public IsbnProvider getProvider() {
        return switch (activeProvider.toLowerCase()) {
            case "googlebooks" -> googleBooksProvider;
            case "openlibrary" -> openLibraryProvider;
            default -> googleBooksProvider; // fallback
        };
    }
}
