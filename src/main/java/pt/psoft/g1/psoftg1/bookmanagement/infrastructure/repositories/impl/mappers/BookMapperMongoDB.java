package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
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

    Book toModel(BookMongoDB bookMongoDB);
    BookMongoDB toMongoDB(Book book);
//
//    default String map(Genre Value){
//        System.out.println("Genre: " + Value);
//        if (Value == null){
//            System.out.println("Genre is null");
//            return null;
//        }
//        System.out.println("Genre is not null: " + Value.getGenre());
//        return Value.getGenre(); // Exemplo para Genre
//    }
//
//    default String map(GenreMongoDB Value){
//        if (Value == null){
//            return null;
//        }
//        return Value.getGenre(); // Exemplo para Genre
//    }
//
//    default String map(Photo photo) {
//        if (photo == null) {
//            return null;
//        }
//        return photo.getPhotoFile();
//    }
//
//    default String map(PhotoMongoDB photoMongoDB) {
//        if (photoMongoDB == null) {
//            return null;
//        }
//        return photoMongoDB.getPhotoFile();
//    }

   default String map(TitleMongoDB value) {
       if (value == null) {
            return null;
       }
       return value.getTitle(); // Exemplo para TitleEntity
   }
//
//    default String map(Title value) {
//        if (value == null) {
//            return null;
//        }
//        return value.getTitle();  // Exemplo para Title
//    }

    default String map(IsbnMongoDB isbnMongoDB) {
        return isbnMongoDB == null ? null : isbnMongoDB.getIsbn();
    }

    default String map(DescriptionMongoDB descriptionMongoDB) {
        return descriptionMongoDB == null ? null : descriptionMongoDB.getDescription();
    }
}