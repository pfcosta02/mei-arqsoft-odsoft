package pt.psoft.g1.psoftg1.lendingmanagement.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LendingQueryDTO {
    private String lendingNumber;        // "2024/1"
    private String bookIsbn;             // ISBN do livro
    private String readerNumber;         // Número do leitor
    private String readerName;           // Nome do leitor
    private LocalDate startDate;
    private LocalDate limitDate;
    private LocalDate returnedDate;
    private String commentary;
    private String status;               // ACTIVE, OVERDUE, RETURNED
    private Integer daysUntilReturn;
    private Integer daysOverdue;
    private int fineValuePerDayInCents;
    private Long version;

    /**
     * Converte um Lending domain para DTO de leitura
     */
    public static LendingQueryDTO from(Lending lending) {
        int daysUntilReturn = 0;
        int daysOverdue = 0;
        String status = "ACTIVE";

        if (lending.getReturnedDate() != null) {
            status = "RETURNED";
        } else {
            long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), lending.getLimitDate());
            if (daysUntil < 0) {
                daysOverdue = (int) Math.abs(daysUntil);
                status = "OVERDUE";
            } else {
                daysUntilReturn = (int) daysUntil;
            }
        }

        return LendingQueryDTO.builder()
                .lendingNumber(lending.getLendingNumber())
                .bookIsbn(lending.getBook().getIsbn().getIsbn())
                .readerNumber(lending.getReaderDetails().getReaderNumber())
                .readerName(lending.getReaderDetails().getReader().getName().getName())
                .startDate(lending.getStartDate())
                .limitDate(lending.getLimitDate())
                .returnedDate(lending.getReturnedDate())
                .commentary(lending.getCommentary())
                .status(status)
                .daysUntilReturn(daysUntilReturn > 0 ? daysUntilReturn : null)
                .daysOverdue(daysOverdue > 0 ? daysOverdue : null)
                .fineValuePerDayInCents(lending.getFineValuePerDayInCents())
                .version(lending.getVersion())
                .build();
    }
}