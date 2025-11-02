package pt.psoft.g1.psoftg1.shared.model.elasticsearch;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.nio.file.Path;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = "photos")
public class PhotoES {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String photoFile;

    public PhotoES(Path photoPath) {
        this.photoFile = photoPath.toString();
    }
}
