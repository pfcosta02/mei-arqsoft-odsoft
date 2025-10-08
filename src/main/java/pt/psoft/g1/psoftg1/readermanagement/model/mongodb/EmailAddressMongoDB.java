package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "email_addresses")
@Profile("mongodb")
@Primary
public class EmailAddressMongoDB implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Field("email")
    @Email
    private String address;

    public EmailAddressMongoDB(String address)
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
