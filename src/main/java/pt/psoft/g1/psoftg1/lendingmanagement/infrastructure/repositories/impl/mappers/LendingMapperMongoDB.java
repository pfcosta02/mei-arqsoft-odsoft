package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoDB;

@Mapper(componentModel = "spring")
public interface LendingMapperMongoDB
{
    Lending toModel(LendingMongoDB entity);
    LendingMongoDB toEntity(Lending model);
}
