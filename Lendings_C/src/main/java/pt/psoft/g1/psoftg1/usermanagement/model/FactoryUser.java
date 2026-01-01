package pt.psoft.g1.psoftg1.usermanagement.model;

import org.springframework.stereotype.Component;

@Component
public class FactoryUser {

    public Reader newReader(String pk, String username, long version) {
        return Reader.newReader(pk, username);
    }

    public Reader newReader(String username) {
        return Reader.newReader(username);
    }

    public Librarian newLibrarian(String username) {
        return Librarian.newLibrarian(username);
    }
}
