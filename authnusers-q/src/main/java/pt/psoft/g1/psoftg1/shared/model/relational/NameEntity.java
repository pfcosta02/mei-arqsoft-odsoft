package pt.psoft.g1.psoftg1.shared.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.shared.model.Name;

@Profile("jpa")
@Primary
@Embeddable
@PropertySource({"classpath:config/library.properties"})
@Getter
public class NameEntity {
    @Column(name = "NAME", length = Name.NAME_MAX_LENGTH)
    @NotNull
    @NotBlank
    @Getter 
    @Setter
    private String name;

    protected NameEntity() 
    {
        // for ORM only
    }

    public NameEntity(String name) 
    {
        setName(name);
    }
}