package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "forbiddenNames")
@NoArgsConstructor
@Profile("mongodb")
public class ForbiddenNameMongoDB {

    @Id
    private String forbiddenNameId;

    @Size(min = 1)
    @Getter @Setter
    private String forbiddenName;

    public ForbiddenNameMongoDB(String forbiddenName) { setForbiddenName(forbiddenName); }
}