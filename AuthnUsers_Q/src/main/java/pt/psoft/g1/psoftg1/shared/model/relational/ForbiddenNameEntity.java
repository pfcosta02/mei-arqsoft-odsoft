package pt.psoft.g1.psoftg1.shared.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ForbiddenName")
@Profile("jpa")
@Primary
@NoArgsConstructor
public class ForbiddenNameEntity 
{
    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    @Column(nullable = false)
    @Size(min = 1)
    private String forbiddenName;

    public ForbiddenNameEntity(String forbiddenName) 
    {
        setForbiddenName(forbiddenName);
    }
}