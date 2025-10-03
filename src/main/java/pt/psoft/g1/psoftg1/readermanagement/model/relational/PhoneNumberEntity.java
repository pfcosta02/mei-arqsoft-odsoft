package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Embeddable
@Profile("jpa")
@Primary
public class PhoneNumberEntity implements Serializable
{
    private String phoneNumber;

    protected PhoneNumberEntity() {}

    public PhoneNumberEntity(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    // Getter
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    // Helper
    public String toString()
    {
        return phoneNumber;
    }
}
