package pt.psoft.g1.psoftg1.lendingmanagement.model.elasticSearch;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * The {@code LendingNumberES} class represents a value object
 * used inside LendingES documents stored in Elasticsearch.
 * It stores a composed identifier in the format "YEAR/SEQUENTIAL".
 */
public class LendingNumberES implements Serializable {

    /**
     * Natural key of a {@code Lending}.
     * Example: "2024/23".
     */
    @Field(type = FieldType.Keyword)
    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    private String lendingNumber;

    /**
     * Constructs a new {@code LendingNumberES} based on year and sequential.
     */
    public LendingNumberES(int year, int sequential) {
        if (year < 1970 || LocalDate.now().getYear() < year)
            throw new IllegalArgumentException("Invalid year component");
        if (sequential < 0)
            throw new IllegalArgumentException("Sequential component cannot be negative");
        this.lendingNumber = year + "/" + sequential;
    }

    /**
     * Constructs a new {@code LendingNumberES} from a formatted string.
     */
    public LendingNumberES(String lendingNumber) {
        if (lendingNumber == null)
            throw new IllegalArgumentException("Lending number cannot be null");

        int year, sequential;
        try {
            year = Integer.parseInt(lendingNumber, 0, 4, 10);
            sequential = Integer.parseInt(lendingNumber, 5, lendingNumber.length(), 10);
            if (lendingNumber.charAt(4) != '/')
                throw new IllegalArgumentException("Lending number has wrong format. It should be \"{year}/{sequential}\"");
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Lending number has wrong format. It should be \"{year}/{sequential}\"");
        }
        this.lendingNumber = year + "/" + sequential;
    }

    /**
     * Constructs a new {@code LendingNumberES} using only sequential,
     * automatically using the current year.
     */
    public LendingNumberES(int sequential) {
        if (sequential < 0)
            throw new IllegalArgumentException("Sequential component cannot be negative");
        this.lendingNumber = LocalDate.now().getYear() + "/" + sequential;
    }

    /** Empty constructor for framework use. */
    public LendingNumberES() {}

    @Override
    public String toString() {
        return this.lendingNumber;
    }
}
