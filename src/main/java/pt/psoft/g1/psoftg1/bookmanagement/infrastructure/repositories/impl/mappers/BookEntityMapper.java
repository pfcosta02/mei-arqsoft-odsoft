package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.TitleEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

@Mapper(componentModel = "spring")
public interface BookEntityMapper
{
    Book toModel(BookEntity entity);
    BookEntity toEntity(Book model);

    default Author map(AuthorEntity value)
    {
        return value == null ? null : new Author(value.getName().toString(), value.getBio().toString(), value.getPhoto().toString());
    }

    default String map(Isbn value)
    {
        return value == null ? null : value.getIsbn();
    }

    default String map(Bio value)
    {
        return value == null ? null : value.getValue();
    }

    default String map(Genre value)
    {
        return value == null ? null : value.getGenre();
    }

    default String map(GenreEntity value)
    {
        return value == null ? null : value.getGenre();
    }

    default String map(Photo photo)
    {
        return photo == null ? null : photo.getPhotoFile();
    }

    default String map(PhotoEntity photoEntity)
    {
        return photoEntity == null ? null : photoEntity.getPhotoFile();
    }

    default String map(TitleEntity value)
    {
        return value == null ? null : value.getTitle();
    }

    default String map(Title value)
    {
        return value == null ? null : value.getTitle();
    }
}