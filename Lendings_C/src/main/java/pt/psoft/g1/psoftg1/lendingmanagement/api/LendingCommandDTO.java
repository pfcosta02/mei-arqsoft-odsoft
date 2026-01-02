package pt.psoft.g1.psoftg1.lendingmanagement.api;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LendingCommandDTO {
    private Long readerId;
    private String bookIsbn;
    private LocalDate startDate;
    private LocalDate limitDate;
    private Integer lendingDurationDays;
    private Integer fineValuePerDayInCents;
}