package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;


import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Profile("mongodb")
@Primary
public class ReaderMongoDB extends UserMongoDB
{
    protected ReaderMongoDB() {
        // for ORM only
    }

    public ReaderMongoDB(String username, String password, Role role)
    {
        super(username, password, role);
    }
}
