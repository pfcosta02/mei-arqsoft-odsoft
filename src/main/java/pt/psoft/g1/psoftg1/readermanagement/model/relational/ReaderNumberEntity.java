package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Embeddable
@Profile("jpa")
@Primary
public class ReaderNumberEntity implements Serializable {

    @Column(name = "READER_NUMBER")
    private String readerNumber;

    protected ReaderNumberEntity() {}

    public ReaderNumberEntity(String readerNumber)
    {
        this.readerNumber = readerNumber;
    }

    public String getReaderNumber()
    {
        return readerNumber;
    }

    public String toString()
    {
        return readerNumber;
    }
}
