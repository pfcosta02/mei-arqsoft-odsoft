package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.shared.model.StringUtilsCustom;

@Document(collection = "names")
@PropertySource({"classpath:config/library.properties"})
@Profile("jpa")
@Primary
public class NameMongoDB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Field("name")  // Optional: Map to a specific field name in MongoDB
    @NotNull
    @Getter
    @Setter
    @NotBlank
    private String name;

    protected NameMongoDB()
    {
        // for ORM only
    }

    public NameMongoDB(String name)
    {
        setName(name);
    }
}
