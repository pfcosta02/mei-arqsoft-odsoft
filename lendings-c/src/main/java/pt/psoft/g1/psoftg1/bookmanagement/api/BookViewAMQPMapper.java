package pt.psoft.g1.psoftg1.bookmanagement.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class BookViewAMQPMapper extends MapperInterface {

    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "version", source = "version")
    public abstract BookViewAMQP toBookViewAMQP(Book book);
    public abstract List<BookViewAMQP> toBookViewAMQP(List<Book> bookList);

    @Mapping(target = "isbn", source = "isbn")
    @Mapping(target = "version", source = "version")
    public abstract Book toBook(BookViewAMQP bookViewAMQP);
    public abstract List<Book> toBook(List<BookViewAMQP> bookViewAMQPList);
}
