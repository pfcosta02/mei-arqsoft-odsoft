package pt.psoft.g1.psoftg1.bookmanagement.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;

/**
 * Brief guide:
 * <a href="https://www.baeldung.com/mapstruct">https://www.baeldung.com/mapstruct</a>
 * */
@Mapper(componentModel = "spring", implementationName = "CustomBookMapperImpl")
public abstract class BookMapper {

    @Mapping(target = "isbn", source = "isbn")
    public abstract Book createBook(CreateBookRequest request);
}
