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
    private Long id;
    private Long readerId;
    private Long bookId;
    private LocalDate startDate;
    private LocalDate limitDate;
    private LocalDate returnedDate;
    private String commentary;
    private String status;
    private Integer daysUntilReturn;
    private Integer daysOverdue;

    // Factory method para converter entity
    public static LendingQueryDTO from(Lending entity) {
        int daysUntilReturn = 0;
        int daysOverdue = 0;
        String status = "ACTIVE";

        if (entity.getReturnedDate() != null) {
            status = "RETURNED";
        } else {
            long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), entity.getLimitDate());
            if (daysUntil < 0) {
                daysOverdue = (int) Math.abs(daysUntil);
                status = "OVERDUE";
            } else {
                daysUntilReturn = (int) daysUntil;
            }
        }

        return LendingQueryDTO.builder()
                .id(entity.getPk())
                .readerId(entity.getReaderDetails() != null ? entity.getReaderDetails().getPk() : null)
                .bookId(entity.getBook() != null ? entity.getBook().getPk() : null)
                .startDate(entity.getStartDate())
                .limitDate(entity.getLimitDate())
                .returnedDate(entity.getReturnedDate())
                .commentary(entity.getCommentary())
                .status(status)
                .daysUntilReturn(daysUntilReturn > 0 ? daysUntilReturn : null)
                .daysOverdue(daysOverdue > 0 ? daysOverdue : null)
                .build();
    }
}