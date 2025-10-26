package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import jakarta.validation.constraints.Email;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "email_addresses")
@Profile("mongodb")
public class EmailAddressMongoDB implements Serializable {

    @Field("email")
    @Email
    private String address;

    protected EmailAddressMongoDB() {}

    public EmailAddressMongoDB(String address)
    {
        this.address = address;
    }

    // Getter
    public String getAddress()
    {
        return address;
    }

    @Override
    public String toString() {
        return address;
    }
}