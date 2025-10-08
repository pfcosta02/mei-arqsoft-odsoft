package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Document(collection = "librarians")
@Profile("mongodb")
@Primary
public class LibrarianMongoDB extends UserMongoDB {

    protected LibrarianMongoDB()
    {
        // for ORM only
    }
    public LibrarianMongoDB(String username, String password, Role role)
    {
        super(username, password, role);
    }
}
