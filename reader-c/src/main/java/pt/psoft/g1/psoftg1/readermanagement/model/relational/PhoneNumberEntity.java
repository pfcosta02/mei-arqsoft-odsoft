package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.*;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

//@Entity
@Embeddable
@Profile("jpa")
@Primary
public class PhoneNumberEntity implements Serializable
{
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long pk;

    private String phoneNumber;

    protected PhoneNumberEntity() {}

    public PhoneNumberEntity(String phoneNumber)
    {
        setPhoneNumber(phoneNumber);
    }

    // Setter
    public void setPhoneNumber(String phoneNumber)
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
