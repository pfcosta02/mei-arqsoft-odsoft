package pt.psoft.g1.psoftg1.shared.model.mongodb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "names")
@PropertySource({"classpath:config/library.properties"})
@Profile("mongodb")
public class NameMongoDB {

    @Id
    private String nameId;

    @Field("name")
    @NotNull
    @Getter @Setter
    @NotBlank
    private String name;

    protected NameMongoDB() {}

    public NameMongoDB(String name)
    {
        setName(name);
    }

    @Override
    public String toString() { return this.name; }
}