package pt.psoft.g1.psoftg1.lendingmanagement.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;
import pt.psoft.g1.psoftg1.shared.util.DateUtils;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class LendingViewAMQPMapper extends MapperInterface {

    @Mapping(target = "lendingNumber", source = "lendingNumber")
    @Mapping(target = "bookIsbn", expression = "java(lending.getBook().getIsbn().toString())")
    @Mapping(target = "readerNumber", expression = "java(lending.getReaderDetails().getReaderNumber())")
    @Mapping(target = "startDate", expression = "java(formatLocalDate(lending.getStartDate()))")
    @Mapping(target = "limitDate", expression = "java(formatLocalDate(lending.getLimitDate()))")
    @Mapping(target = "fineValuePerDayInCents", expression = "java(lending.getFineValuePerDayInCents())")
    @Mapping(target = "commentary", source = "commentary")
    @Mapping(target = "version", source = "version")
    public abstract LendingViewAMQP toLendingViewAMQP(Lending lending);
    public abstract List<LendingViewAMQP> toLendingViewAMQP(List<Lending> lendingList);

    // Utility method to format LocalDate -> String
    protected String formatLocalDate(LocalDate date) {
        return date != null ? DateUtils.ISO_DATE_FORMATTER.format(date) : null;
    }
}
