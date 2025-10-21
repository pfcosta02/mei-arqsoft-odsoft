package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.DescriptionEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.IsbnEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.TitleEntity;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreEntityMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoEntityMapper;

@Mapper(componentModel = "spring", uses = {GenreEntityMapper.class, AuthorEntityMapper.class, PhotoEntityMapper.class})
public interface BookEntityMapper
{
    Book toModel(BookEntity entity);
    BookEntity toEntity(Book model);

//    default Author map(AuthorEntity value)
//    {
//        return value == null ? null : new Author(value.getName().toString(), value.getBio().toString(), value.getPhoto().toString());
//    }
//
//    default String map(Isbn value)
//    {
//        return value == null ? null : value.getIsbn();
//    }
//
//    default String map(Bio value)
//    {
//        return value == null ? null : value.getValue();
//    }
//
//    default String map(Genre value)
//    {
//        return value == null ? null : value.getGenre();
//    }
//
//    default String map(GenreEntity value)
//    {
//        return value == null ? null : value.getGenre();
//    }
//
//    default String map(Photo photo)
//    {
//        return photo == null ? null : photo.getPhotoFile();
//    }
//
//    default String map(PhotoEntity photoEntity)
//    {
//        return photoEntity == null ? null : photoEntity.getPhotoFile();
//    }

    default String map(TitleEntity value)
    {
        return value == null ? null : value.getTitle();
    }

//    default String map(Title value)
//    {
//        return value == null ? null : value.getTitle();
//    }

    default String map(IsbnEntity entity) {
        return entity == null ? null : entity.getIsbn();
    }

    default String map(DescriptionEntity entity) {
        return entity == null ? null : entity.getDescription();
    }
}