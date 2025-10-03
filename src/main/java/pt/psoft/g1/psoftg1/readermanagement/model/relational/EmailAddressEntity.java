package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Embeddable
@Profile("jpa")
@Primary
public class EmailAddressEntity implements Serializable
{
    @Email
    private String address;

    protected EmailAddressEntity() {}

    public EmailAddressEntity(String address)
    {
        this.address = address;
    }

    // Getter
    public String getAddress()
    {
        return address;
    }

    // Helper
    public String toString() {
        return address;
    }
}

