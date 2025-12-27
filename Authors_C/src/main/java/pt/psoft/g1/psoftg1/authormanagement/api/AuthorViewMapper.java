package pt.psoft.g1.psoftg1.authormanagement.api;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class AuthorViewMapper extends MapperInterface {

    @Mapping(target = "_links", expression = "java(mapLinks(author))")
    public abstract AuthorView toAuthorView(Author author);

    public abstract List<AuthorView> toAuthorView(List<Author> authors);

    public abstract AuthorCoAuthorBooksView toAuthorCoAuthorBooksView(Author author, List<CoAuthorView> coauthors);

    @Named(value = "mapAuthorLinks")
    public Map<String, Object> mapLinks(final Author author){
        String authorUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/authors/")
                .path(author.getAuthorNumber())
                .toUriString();

        String booksByAuthorUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/authors/")
                .path(author.getAuthorNumber())
                .path("/books")
                .toUriString();

        Map<String, Object> links = new HashMap<>();

        links.put("author", authorUri);
        links.put("photo", generatePhotoUrl(author));
        links.put("booksByAuthor", booksByAuthorUri);

        return links;
    }

    protected String generatePhotoUrl(Author author) {
        String authorNumber = author.getAuthorNumber();
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/authors/{authorNumber}/photo").buildAndExpand(authorNumber).toUri().toString();
    }
}
