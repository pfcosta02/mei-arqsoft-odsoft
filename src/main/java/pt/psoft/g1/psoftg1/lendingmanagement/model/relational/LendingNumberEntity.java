package pt.psoft.g1.psoftg1.lendingmanagement.model.relational;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Embeddable
@EqualsAndHashCode
@Profile("jpa")
@Primary
public class LendingNumberEntity implements Serializable
{

    @Column(name = "LENDING_NUMBER", length = 32)
    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    private String lendingNumber;

    protected LendingNumberEntity() { }

    public LendingNumberEntity(String lendingNumber)
    {
        setValue(lendingNumber);
    }

    // Getter
    public String getValue()
    {
        return lendingNumber;
    }

    // Setter
    public void setValue(String lendingNumber)
    {
        this.lendingNumber = lendingNumber;
    }

    // Helper
    public String toString()
    {
        return this.lendingNumber;
    }
}
