package pt.psoft.g1.psoftg1.isbnmanagement.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.isbnmanagement.impl.FallbackIsbnProvider;
import pt.psoft.g1.psoftg1.isbnmanagement.impl.GoogleBooksProvider;
import pt.psoft.g1.psoftg1.isbnmanagement.impl.OpenLibraryProvider;
import pt.psoft.g1.psoftg1.isbnmanagement.infrastructure.IsbnProvider;

@Configuration
public class IsbnProviderFactory {

    private final GoogleBooksProvider googleBooksProvider;

    private final OpenLibraryProvider openLibraryProvider;

    private final FallbackIsbnProvider fallbackIsbnProvider;

    @Value("${isbn.provider}")
    private String activeProvider;

    @Autowired
    public IsbnProviderFactory(GoogleBooksProvider googleBooksProvider, OpenLibraryProvider openLibraryProvider, FallbackIsbnProvider fallbackIsbnProvider) {
        this.googleBooksProvider = googleBooksProvider;
        this.openLibraryProvider = openLibraryProvider;
        this.fallbackIsbnProvider = fallbackIsbnProvider;
    }

    public IsbnProvider getProvider() {
        return switch (activeProvider.toLowerCase()) {
            case "googlebooks" -> googleBooksProvider;
            case "openlibrary" -> openLibraryProvider;
            case "gb_and_ol", "fallback" -> fallbackIsbnProvider;
            default -> fallbackIsbnProvider; // fallback
        };
    }
}
