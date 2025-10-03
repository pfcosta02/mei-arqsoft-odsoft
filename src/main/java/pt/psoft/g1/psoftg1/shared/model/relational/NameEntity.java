package pt.psoft.g1.psoftg1.shared.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Embeddable
@PropertySource({"classpath:config/library.properties"})
@Profile("jpa")
@Primary
public class NameEntity {
    @Column(name = "NAME", length = 150)
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


