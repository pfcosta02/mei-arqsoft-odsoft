package pt.psoft.g1.psoftg1.authormanagement.model.elasticsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
public class BioES {

    @Field(type = FieldType.Text)
    private String bio;

    public BioES(String bio) {
        if (bio != null && !bio.isBlank()) {
            this.bio = bio;
        }
    }

    @Override
    public String toString() {
        return this.bio;
    }
}