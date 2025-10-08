package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "forbiddenNames")
@NoArgsConstructor
@Profile("mongodb")
@Primary
public class ForbiddenNameMongoDB {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Getter
    @Setter
    @Column(nullable = false)
    @Size(min = 1)
    private String forbiddenName;

    public ForbiddenNameMongoDB(String forbiddenName)
    {
        setForbiddenName(forbiddenName);
    }
}
