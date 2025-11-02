package pt.psoft.g1.psoftg1.shared.model.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameES {

    @Field(type = FieldType.Text)
    private String name;

    public void setName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null");
        if (name.isBlank())
            throw new IllegalArgumentException("Name cannot be blank, nor only white spaces");
        if (!StringUtilsCustom.isAlphanumeric(name))
            throw new IllegalArgumentException("Name can only contain alphanumeric characters");

        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}