package pt.psoft.g1.psoftg1.shared.model.elasticsearch;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(indexName = "forbiddennames")
public class ForbiddenNameES {

    @Id
    private String id; // Elasticsearch usa String como ID por padrão

    @Getter
    @Setter
    @Field(type = FieldType.Text)
    private String forbiddenName;

    public ForbiddenNameES(String name) {
        this.forbiddenName = name;
    }
}