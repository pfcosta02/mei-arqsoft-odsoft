package pt.psoft.g1.psoftg1.usermanagement.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.persistence.Entity;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;

@Entity
@Profile("jpa")
@Primary
public class LibrarianEntity extends UserEntity
{
    protected LibrarianEntity()
    {
        // for ORM only
    }
    public LibrarianEntity(String username, String password, Role role)
    {
        super(username, password, role);
    }
}
