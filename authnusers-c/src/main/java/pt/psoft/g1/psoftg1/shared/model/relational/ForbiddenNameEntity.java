package pt.psoft.g1.psoftg1.shared.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Table(name="ForbiddenName")
@NoArgsConstructor
@Profile("jpa")
@Primary
public class ForbiddenNameEntity {
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
