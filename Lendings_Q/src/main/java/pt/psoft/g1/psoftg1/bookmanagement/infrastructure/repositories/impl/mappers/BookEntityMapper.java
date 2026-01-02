package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.DescriptionEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.IsbnEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.TitleEntity;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoEntityMapper;

@Mapper(componentModel = "spring", uses = {PhotoEntityMapper.class})
public interface BookEntityMapper
{
    Book toModel(BookEntity entity);
    BookEntity toEntity(Book model);


    default String map(TitleEntity value)
    {
        return value == null ? null : value.getTitle();
    }

    default String map(IsbnEntity entity) {
        return entity == null ? null : entity.getIsbn();
    }

    default String map(DescriptionEntity entity) {
        return entity == null ? null : entity.getDescription();
    }

}