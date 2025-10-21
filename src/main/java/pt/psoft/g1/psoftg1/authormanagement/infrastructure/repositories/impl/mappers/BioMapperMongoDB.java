package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.BioMongoDB;

@Mapper(componentModel = "spring")
public interface BioMapperMongoDB {
    Bio toModel(BioMongoDB entity);
    BioMongoDB toEntity(Bio model);

    default Bio map(String value)
    {
        return value == null ? null : new Bio(value);
    }
}