package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document
@Profile("mongodb")
public class ReaderNumberMongoDB implements Serializable {

    @Field("reader_number")
    private String readerNumber;

    protected ReaderNumberMongoDB() {}

    public ReaderNumberMongoDB(String readerNumber) { this.readerNumber = readerNumber; }

    public String getReaderNumber() { return readerNumber; }

    @Override
    public String toString() { return this.readerNumber; }
}