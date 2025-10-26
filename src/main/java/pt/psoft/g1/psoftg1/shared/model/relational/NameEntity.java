package pt.psoft.g1.psoftg1.shared.model.relational;

import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Embeddable
@PropertySource({"classpath:config/library.properties"})
@Profile("jpa")
@Primary
public class NameEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long pk;

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

    @Override
    public String toString() { return this.name; }
}


