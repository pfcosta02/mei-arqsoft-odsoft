package pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;
import lombok.Setter;

/**
 * Elasticsearch version of ReaderES
 */
@Getter
@Setter
@Document(indexName = "users")
public class ReaderES extends UserES {

    @Field(type = FieldType.Keyword)
    private final String type = "ReaderES";

    protected ReaderES() {
        super();
    }

    public ReaderES(String username, String password) {
        super(username, password);
        this.addAuthority(new RoleES(RoleES.READER));
    }

    public static ReaderES newReader(final String username, final String password, final String name) {
        final var u = new ReaderES(username, password);
        u.setName(name);
        return u;
    }
}
