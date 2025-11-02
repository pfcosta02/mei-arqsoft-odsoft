package pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
public class ReaderNumberES implements Serializable {

    @Getter
    @Field(type = FieldType.Keyword)
    private String readerNumber;

    public ReaderNumberES(int year, int number) {
        this.readerNumber = year + "/" + number;
    }

    public ReaderNumberES(int number) {
        this.readerNumber = LocalDate.now().getYear() + "/" + number;
    }

    @Override
    public String toString() {
        return readerNumber;
    }
}