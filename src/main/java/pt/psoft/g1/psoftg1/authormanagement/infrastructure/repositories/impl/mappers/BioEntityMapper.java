package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.BioEntity;

@Mapper(componentModel = "spring")
public interface BioEntityMapper {
    Bio toModel(BioEntity entity);
    BioEntity toEntity(Bio model);

    default Bio map(String value)
    {
        return value == null ? null : new Bio(value);
    }
}
