package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingNumber;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingNumberEntity;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = { ReaderDetailsEntityMapper.class, BookEntityMapper.class})
public interface LendingEntityMapper 
{
    Lending toModel(LendingEntity entity);
    LendingEntity toEntity(Lending model);

    default LendingNumber map(LendingNumberEntity entity)
    {
        return entity == null ? null : new LendingNumber(entity.getLendingNumber());
    }

    default LendingNumberEntity map(LendingNumber number)
    {
        return number == null ? null : new LendingNumberEntity(number.getLendingNumber());
    }

    default LendingNumberEntity map(String value)
    {
        return value == null ? null : new LendingNumberEntity(value);
    }

    default Integer map(Optional<Integer> value) {
        return value.orElse(null);
    }

    default Optional<Integer> map(Integer value) {
        return Optional.ofNullable(value);
    }

    default Author map(AuthorEntity value)
    {
        return value == null ? null : new Author(value.getName().toString(), value.getBio().toString(), value.getPhoto().toString());
    }

    default String map(NameEntity value)
    {
        return value == null ? null : value.getName();
    }

    default String map(Bio value)
    {
        return value == null ? null : value.getValue();
    }
}