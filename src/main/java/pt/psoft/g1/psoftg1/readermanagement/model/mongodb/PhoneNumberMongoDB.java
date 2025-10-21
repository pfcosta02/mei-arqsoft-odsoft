package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "phone_number")
@Profile("mongodb")
public class PhoneNumberMongoDB implements Serializable {

    @Id
    private String phoneNumberId;

    @Field("phone_number")
    private String phoneNumber;

    public PhoneNumberMongoDB(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    protected PhoneNumberMongoDB() {}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @Override
    public String toString() { return this.phoneNumber; }
}

