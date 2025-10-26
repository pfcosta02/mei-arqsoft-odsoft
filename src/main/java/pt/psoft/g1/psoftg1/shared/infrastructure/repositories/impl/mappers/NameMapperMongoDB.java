package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;

@Mapper(componentModel = "spring")
public interface NameMapperMongoDB {

    Name toModel(NameMongoDB entity);

    NameMongoDB toMongoDB(Name model);

    default String map(NameMongoDB value)
    {
        return value == null ? null : value.getName();
    }

    default String map(Name value)
    {
        return value == null ? null : value.getName();
    }

    default Name map(String value)
    {
        return value == null ? null : new Name(value);
    }
}