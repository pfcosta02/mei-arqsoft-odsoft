package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Entity
@Embeddable
@PropertySource({"classpath:config/library.properties"})
@Profile("jpa")
@Primary
public class BirthDateEntity implements Serializable
{
    @Getter
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    protected BirthDateEntity() {}

    public BirthDateEntity(LocalDate birthDate)
    {
        this.birthDate = birthDate;
    }
}
