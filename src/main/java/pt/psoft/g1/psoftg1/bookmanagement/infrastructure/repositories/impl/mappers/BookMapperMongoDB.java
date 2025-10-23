package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorMapperMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.DescriptionMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.IsbnMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.TitleMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreMapperMongoDB;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoMapperMongoDB;

@Mapper(componentModel = "spring", uses = {GenreMapperMongoDB.class, AuthorMapperMongoDB.class, PhotoMapperMongoDB.class})
public interface BookMapperMongoDB {

    @Mapping(target="photoURI", source="photo")
    Book toModel(BookMongoDB bookMongoDB);

    @Mapping(target = "bookId", ignore = true)
    BookMongoDB toMongoDB(Book book);

   default String map(TitleMongoDB titleMongoDB) {
       return titleMongoDB == null ? null : titleMongoDB.getTitle();
   }

    default String map(IsbnMongoDB isbnMongoDB) {
        return isbnMongoDB == null ? null : isbnMongoDB.getIsbn();
    }

    default String map(DescriptionMongoDB descriptionMongoDB) {
        return descriptionMongoDB == null ? null : descriptionMongoDB.getDescription();
    }
}