package pt.psoft.g1.psoftg1.shared.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Profile("jpa")
@Table(name="Photo")
@Primary
public class PhotoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long pk;

    @Getter
    @Setter
    private String photoFile;

    protected PhotoEntity() { }

    public PhotoEntity(String photoFile)
    {
        setPhotoFile(photoFile);
    }
}
