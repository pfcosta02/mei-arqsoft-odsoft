package pt.psoft.g1.psoftg1.usermanagement.model.mongodb;

import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Profile("mongodb")
public class ReaderMongoDB extends UserMongoDB
{
    protected ReaderMongoDB() {}

    public ReaderMongoDB(String username, String password, Role role)
    {
        super(username, password, role);
    }
}