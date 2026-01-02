package pt.psoft.g1.psoftg1.lendingmanagement.api;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingCommandDTO {
    @NotNull(message = "Book ISBN cannot be null")
    private String bookIsbn;              // ISBN do livro

    @NotNull(message = "Reader number cannot be null")
    private String readerNumber;          // Número do leitor

//    @Positive(message = "Lending duration must be positive")
//    private int lendingDurationDays;      // Ex: 14 dias
//
//    @Positive(message = "Fine value must be positive")
//    private int fineValuePerDayInCents;   // Ex: 50 (centavos por dia)
}