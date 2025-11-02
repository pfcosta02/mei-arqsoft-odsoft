package pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;

@NoArgsConstructor
public class BirthDateES {

    @Getter
    @Field(type = FieldType.Date)
    private LocalDate birthDate;

    private final String dateFormatRegexPattern = "\\d{4}-\\d{2}-\\d{2}";
    private int minimumAge = 18; // podes injectar via configuração se precisares

    public BirthDateES(int year, int month, int day) {
        setBirthDate(year, month, day);
    }

    public BirthDateES(String birthDate) {
        birthDate = birthDate.replace("/", "-");

        if(!birthDate.matches(dateFormatRegexPattern)) {
            throw new IllegalArgumentException("Provided birth date is not in a valid format. Use yyyy-MM-dd");
        }
        String[] parts = birthDate.split("-");
        setBirthDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }

    private void setBirthDate(int year, int month, int day) {
        LocalDate minimumDate = LocalDate.now().minusYears(minimumAge);
        LocalDate userDate = LocalDate.of(year, month, day);
        if(userDate.isAfter(minimumDate)) {
            throw new AccessDeniedException("User must be at least " + minimumAge + " years old");
        }
        this.birthDate = userDate;
    }

    @Override
    public String toString() {
        return String.format("%d-%d-%d", birthDate.getYear(), birthDate.getMonthValue(), birthDate.getDayOfMonth());
    }
}
