package pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@NoArgsConstructor
public class IsbnES {

    @Size(min = 10, max = 13)
    @Field(type = FieldType.Keyword) // ISBN é exato, então Keyword é mais adequado
    @Getter
    @Setter
    private String isbn;

    public IsbnES(String isbn) {
        setIsbn(isbn);
    }

    public void setIsbn(String isbn) {
        if (isbn == null)
            throw new IllegalArgumentException("Isbn cannot be null");
        if (!isValidIsbn(isbn))
            throw new IllegalArgumentException("Invalid ISBN-13 format or check digit.");
        this.isbn = isbn;
    }

    private static boolean isValidIsbn(String isbn) {
        return (isbn.length() == 10) ? isValidIsbn10(isbn) : isValidIsbn13(isbn);
    }

    private static boolean isValidIsbn10(String isbn) {
        if (!isbn.matches("\\d{9}[\\dX]")) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (isbn.charAt(i) - '0') * (10 - i);
        int lastDigit = (isbn.charAt(9) == 'X') ? 10 : isbn.charAt(9) - '0';
        sum += lastDigit;
        return sum % 11 == 0;
    }

    private static boolean isValidIsbn13(String isbn) {
        if (!isbn.matches("\\d{13}")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Integer.parseInt(isbn.substring(i, i + 1));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checksum = 10 - (sum % 10);
        if (checksum == 10) checksum = 0;
        return checksum == Integer.parseInt(isbn.substring(12));
    }

    @Override
    public String toString() {
        return this.isbn;
    }
}

