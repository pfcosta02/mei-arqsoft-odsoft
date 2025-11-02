package pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@NoArgsConstructor
public class PhoneNumberES {

    @Getter
    @Field(type = FieldType.Keyword)
    private String phoneNumber;

    public PhoneNumberES(String phoneNumber) {
        setPhoneNumber(phoneNumber);
    }

    private void setPhoneNumber(String number) {
        if (!(number.startsWith("9") || number.startsWith("2")) || number.length() != 9) {
            throw new IllegalArgumentException("Phone number is not valid: " + number);
        }
        this.phoneNumber = number;
    }

    @Override
    public String toString() {
        return phoneNumber;
    }
}
