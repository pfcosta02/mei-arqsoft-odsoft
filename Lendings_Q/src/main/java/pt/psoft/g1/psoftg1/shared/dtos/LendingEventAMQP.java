package pt.psoft.g1.psoftg1.shared.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingEventAMQP {
    public String lendingNumber;      // "2024/1", "2024/2", etc
    public String bookIsbn;            // ISBN do livro (string)
    public String readerNumber;        // Número do leitor (string)
    public LocalDate startDate;
    public LocalDate limitDate;
    public LocalDate returnedDate;
    public String commentary;
    public Integer rating;
    public Long version;
    public int fineValuePerDayInCents;


    public static LendingEventAMQP from(Lending lending) {
        return LendingEventAMQP.builder()
                .lendingNumber(lending.getLendingNumber())
                .bookIsbn(lending.getBook().getIsbn().getIsbn())
                .readerNumber(lending.getReaderDetails().getReaderNumber())
                .startDate(lending.getStartDate())
                .limitDate(lending.getLimitDate())
                .returnedDate(lending.getReturnedDate())
                .commentary(lending.getCommentary())
                .rating(lending.getRating())
                .version(lending.getVersion())
                .fineValuePerDayInCents(lending.getFineValuePerDayInCents())
                .build();
    }

}

