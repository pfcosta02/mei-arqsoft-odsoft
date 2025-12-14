package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDate;

@PropertySource({"classpath:config/library.properties"})
@Document(collection = "birth_date")
@Profile("mongodb")
public class BirthDateMongoDB implements Serializable {

    @Id
    private String birthDateId;

    @Field("birth_date")
    @Getter
    LocalDate birthDate;

    @Value("${minimumReaderAge}")
    @Field("minimum_age")
    private int minimumAge;

    protected BirthDateMongoDB() {}

    public BirthDateMongoDB(LocalDate birthDate) { this.birthDate = birthDate; }
}