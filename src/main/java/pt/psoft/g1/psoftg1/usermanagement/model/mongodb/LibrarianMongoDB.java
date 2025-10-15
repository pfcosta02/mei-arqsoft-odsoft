package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Document(collection = "librarians")
@Profile("mongodb")
public class LibrarianMongoDB extends UserMongoDB {

    protected LibrarianMongoDB() {}

    public LibrarianMongoDB(String username, String password, Role role)
    {
        super(username, password, role);
    }
}