package pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

@NoArgsConstructor
public class DescriptionES {

    private static final int DESC_MAX_LENGTH = 4096;

    @Size(max = DESC_MAX_LENGTH)
    @Field(type = FieldType.Text) // Texto livre, analisado
    @Getter
    @Setter
    private String description;

    public DescriptionES(String description) {
        setDescription(description);
    }

    public void setDescription(@Nullable String description) {
        if (description == null || description.isBlank()) {
            this.description = null;
        } else if (description.length() > DESC_MAX_LENGTH) {
            throw new IllegalArgumentException("Description has a maximum of 4096 characters");
        } else {
            this.description = StringUtilsCustom.sanitizeHtml(description);
        }
    }

    @Override
    public String toString() {
        return this.description;
    }
}
