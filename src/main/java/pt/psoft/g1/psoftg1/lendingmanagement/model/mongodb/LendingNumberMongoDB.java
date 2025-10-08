package pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "lendings_numbers")
@EqualsAndHashCode
@Profile("mongodb")
@Primary
public class LendingNumberMongoDB {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Column(name = "LENDING_NUMBER", length = 32)
    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    private String lendingNumber;

    protected LendingNumberMongoDB(){
    }

    public LendingNumberMongoDB(int year, int sequential){
        if(year < 1970 || LocalDate.now().getYear() < year)
            throw new IllegalArgumentException("Invalid year component");
        if(sequential < 0)
            throw new IllegalArgumentException("Sequential component cannot be negative");
        this.lendingNumber = year + "/" + sequential;
    }

    @Builder
    public LendingNumberMongoDB(String lendingNumber) {
        if (lendingNumber == null)
            throw new IllegalArgumentException("Lending number cannot be null");
        int year, sequential;
        try {
            year = Integer.parseInt(lendingNumber.substring(0, 4));
            sequential = Integer.parseInt(lendingNumber.substring(5));
            if (lendingNumber.charAt(4) != '/')
                throw new IllegalArgumentException("Lending number has wrong format. It should be \"{year}/{sequential}\"");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Lending number has wrong format. It should be \"{year}/{sequential}\"");
        }
        this.lendingNumber = year + "/" + sequential;
    }

    public LendingNumberMongoDB(int sequential){
        if (sequential < 0)
            throw new IllegalArgumentException("Sequential component cannot be negative");
        this.lendingNumber = LocalDate.now().getYear() + "/" + sequential;
    }

    public String getLendingNumber(){
        return lendingNumber;
    }

    @Override
    public String toString(){
        return this.lendingNumber;
    }
}