package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorRedisMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.model.redis.BookRedisDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreRedisMapper;
import pt.psoft.g1.psoftg1.shared.model.Photo;

@Mapper(componentModel = "spring", uses = {GenreRedisMapper.class, AuthorRedisMapper.class})
public interface BookRedisMapper {


    @Mappings({
            @Mapping(target = "isbn", expression = "java(book.getIsbn().getIsbn())"),
            @Mapping(target = "title", expression = "java(book.getTitle().getTitle())"),
            @Mapping(target = "description", expression = "java(book.getDescription().getDescription())"),
            @Mapping(target = "genre", source = "genre"),
            @Mapping(target = "authors", source = "authors"),
            @Mapping(target = "photoURI", source = "photo", qualifiedByName = "mapPhotBook")
    })
    BookRedisDTO toDTO(Book book);

    @Mappings({
            @Mapping(target = "isbn", source = "isbn"),
            @Mapping(target = "title", source = "title"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "genre", source = "genre"),
            @Mapping(target = "authors", source = "authors")
    })
    Book toDomain(BookRedisDTO dto);


    default Isbn mapIsbn(String isbn) {
        return new Isbn(isbn);
    }

    default Title mapTitle(String title) {
        return new Title(title);
    }

    default Description mapDescription(String description) {
        return new Description(description);
    }

    @Named("mapPhotBook")
    default String mapPhoto(Photo photo) {
        return photo != null ? photo.getPhotoFile() : null;
    }

}
