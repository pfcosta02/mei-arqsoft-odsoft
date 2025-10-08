package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document
@Profile("mongodb")
@Primary
public class ReaderNumberMongoDB implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Field("reader_number")
    private String readerNumber;

    protected ReaderNumberMongoDB() {}

    public ReaderNumberMongoDB(String readerNumber)
    {
        this.readerNumber = readerNumber;
    }

    public String getReaderNumber()
    {
        return readerNumber;
    }

    public String toString() {
        return this.readerNumber;
    }
}