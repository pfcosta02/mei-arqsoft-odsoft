package pt.psoft.g1.psoftg1.isbn.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.isbn.impl.GoogleBooksProvider;
import pt.psoft.g1.psoftg1.isbn.infrasctructure.IsbnProvider;

@Component
public class IsbnProviderFactory {

    private final GoogleBooksProvider googleBooksProvider;

    @Value("${isbn.provider}")
    private String activeProvider;

    @Autowired
    public IsbnProviderFactory(GoogleBooksProvider googleBooksProvider) {
        this.googleBooksProvider = googleBooksProvider;
    }

    public IsbnProvider getProvider() {
        return switch (activeProvider.toLowerCase()) {
            case "googlebooks" -> googleBooksProvider;
            default -> googleBooksProvider; // fallback
        };
    }
}
