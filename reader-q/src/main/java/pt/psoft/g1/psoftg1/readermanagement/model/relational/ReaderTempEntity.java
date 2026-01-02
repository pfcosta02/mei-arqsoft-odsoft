package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("jpa")
@Primary
@Entity
@Table(name = "readers_temp")
public class ReaderTempEntity
{
    @Id
    @Setter
    @Getter
    private String readerId;

    @Setter
    @Getter
    private String userId;

    @Column(nullable = false)
    @Setter
    @Getter
    private String name;

    @Column(nullable = false)
    @Setter
    @Getter
    private String email;
}
