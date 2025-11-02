package pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;
import lombok.Setter;

/**
 * Elasticsearch version of Librarian
 */
@Getter
@Setter
@Document(indexName = "users")
public class LibrarianES extends UserES {

    @Field(type = FieldType.Keyword)
    private final String type = "LIBRARIAN";

    protected LibrarianES() {
        super();
    }

    public LibrarianES(String username, String password) {
        super(username, password);
    }

    public static LibrarianES newLibrarian(final String username, final String password, final String name) {
        final var u = new LibrarianES(username, password);
        u.setName(name);
        u.addAuthority(new RoleES(RoleES.LIBRARIAN));
        return u;
    }
}


