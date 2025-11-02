package pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.validation.constraints.Email;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmailAddressES {

    @Email
    @Field(type = FieldType.Keyword)
    private String address;
}
