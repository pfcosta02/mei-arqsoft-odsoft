package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookMapperMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingNumber;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingNumberMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsMapperMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;

import java.util.Optional;

@Mapper(componentModel = "spring", uses = { ReaderDetailsMapperMongoDB.class, BookMapperMongoDB.class})
public interface LendingMapperMongoDB
{
    Lending toModel(LendingMongoDB entity);
    LendingMongoDB toEntity(Lending model);

    default LendingNumber map(LendingNumberMongoDB entity)
    {
        return entity == null ? null : new LendingNumber(entity.getLendingNumber());
    }

    default LendingNumberMongoDB map(LendingNumber number)
    {
        return number == null ? null : new LendingNumberMongoDB(number.getLendingNumber());
    }

    default LendingNumberMongoDB map(String value)
    {
        return value == null ? null : new LendingNumberMongoDB(value);
    }

    default Integer map(Optional<Integer> value) {
        return value.orElse(null);
    }

    default Optional<Integer> map(Integer value) {
        return Optional.ofNullable(value);
    }

    default Author map(AuthorMongoDB value)
    {
        return value == null ? null : new Author(value.getName().toString(), value.getBio().toString(), value.getPhoto().toString());
    }

    default String map(NameMongoDB value)
    {
        return value == null ? null : value.getName();
    }

    default String map(Bio value)
    {
        return value == null ? null : value.getValue();
    }
}
