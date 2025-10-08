package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "phone_number")
@Profile("mongodb")
@Primary
public class PhoneNumberMongoDB implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Field("phone_number")
    private String phoneNumber;

    public PhoneNumberMongoDB(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    protected PhoneNumberMongoDB() {}

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String toString() {
        return this.phoneNumber;
    }
}

