package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoDB;

@Mapper(componentModel = "spring", uses = { LendingMapperMongoDB.class})
public interface FineMapperMongoDB {
    Fine toModel(FineMongoDB entity);
    FineMongoDB toEntity(Fine model);

    default String map(Bio value)
    {
        return value == null ? null : value.getValue();
    }
}
