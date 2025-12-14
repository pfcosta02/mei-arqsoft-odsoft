package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.*;

import java.io.Serializable;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

//@Entity
@Embeddable
@Profile("jpa")
@Primary
public class ReaderNumberEntity implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long pk;

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
