package pt.psoft.g1.psoftg1.lendingmanagement.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;

import java.util.List;

/**
 * Brief guides:
 * <a href="https://www.baeldung.com/mapstruct">https://www.baeldung.com/mapstruct</a>
 * <p>
 * <a href="https://medium.com/@susantamon/mapstruct-a-comprehensive-guide-in-spring-boot-context-1e7202da033e">https://medium.com/@susantamon/mapstruct-a-comprehensive-guide-in-spring-boot-context-1e7202da033e</a>
 * */
@Mapper(componentModel = "spring")
public abstract class LendingViewMapper extends MapperInterface {

    @Mapping(target = "lendingNumber", source = "lendingNumber")
    @Mapping(target = "bookIsbn", expression = "java(lending.getBook().getIsbn().toString())")
    @Mapping(target = "readerNumber", expression = "java(lending.getReaderDetails().getReaderNumber())")
    @Mapping(target = "fineValueInCents", expression = "java(lending.getFineValueInCents().orElse(null))")
    @Mapping(target = "_links.self", source = ".", qualifiedByName = "lendingLink")
    @Mapping(target = "_links.book", source = "book", qualifiedByName = "bookLink")
    @Mapping(target = "returnedDate", source = "returnedDate")
    @Mapping(target = "commentary", source = "commentary")
    @Mapping(target = "rating", source = "rating")
    @Mapping(target = "_links.reader", source = "readerDetails", qualifiedByName = "readerLink")
    public abstract LendingView toLendingView(Lending lending);

    public abstract List<LendingView> toLendingView(List<Lending> lendings);

    public abstract LendingsAverageDurationView toLendingsAverageDurationView(Double lendingsAverageDuration);
}
