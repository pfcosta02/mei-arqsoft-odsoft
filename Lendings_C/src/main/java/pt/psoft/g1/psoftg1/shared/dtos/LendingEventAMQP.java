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
    public Long id;
    public Long readerId;
    public Long bookId;
    public LocalDate startDate;
    public LocalDate limitDate;
    public LocalDate returnedDate;
    public String commentary;
    public String status;
    public Long version;

    public static LendingEventAMQP from(Lending lending) {
        return LendingEventAMQP.builder()
                .id(lending.getPk())
                .readerId(lending.getReaderDetails().getPk())
                .bookId(lending.getBook().getPk())
                .startDate(lending.getStartDate())
                .limitDate(lending.getLimitDate())
                .returnedDate(lending.getReturnedDate())
                .commentary(lending.getCommentary())
                .version(lending.getVersion())
                .build();
    }
}

