package pt.psoft.g1.psoftg1.readermanagement.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class PhoneNumber {
    private final String phoneNumber;

    // Construtor único que aceita a string direta
    @JsonCreator
    public PhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        if (!(phoneNumber.startsWith("9") || phoneNumber.startsWith("2")) || phoneNumber.length() != 9) {
            throw new IllegalArgumentException("Phone number is not valid: " + phoneNumber);
        }
        this.phoneNumber = phoneNumber;
    }

    // Getter
    @JsonValue // permite que Jackson serialize de volta como string
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return phoneNumber;
    }
}
