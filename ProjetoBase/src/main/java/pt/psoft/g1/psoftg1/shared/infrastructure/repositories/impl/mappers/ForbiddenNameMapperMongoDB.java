package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.mongodb.ForbiddenNameMongoDB;

@Mapper(componentModel = "spring")
public interface ForbiddenNameMapperMongoDB {
    ForbiddenName toModel(ForbiddenNameMongoDB entity);
    ForbiddenNameMongoDB toMongoDB(ForbiddenName model);
}