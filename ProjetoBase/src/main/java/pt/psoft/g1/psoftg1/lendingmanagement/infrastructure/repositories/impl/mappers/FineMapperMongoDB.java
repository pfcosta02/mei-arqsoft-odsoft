package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoDB;

@Mapper(componentModel = "spring")
public interface FineMapperMongoDB {
    Fine toModel(FineMongoDB entity);
    FineMongoDB toEntity(Fine model);
}
