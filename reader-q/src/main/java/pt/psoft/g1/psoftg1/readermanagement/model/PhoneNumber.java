package pt.psoft.g1.psoftg1.readermanagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = PhoneNumberDeserializer.class)
public class PhoneNumber {
    private String phoneNumber;

    public PhoneNumber() {} // opcional

    public PhoneNumber(String phoneNumber) {
        setPhoneNumber(phoneNumber);
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (!(phoneNumber.startsWith("9") || phoneNumber.startsWith("2")) || phoneNumber.length() != 9) {
            throw new IllegalArgumentException("Phone number is not valid: " + phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() { return phoneNumber; }
}

