package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;

@Mapper(componentModel = "spring")
public interface LendingEntityMapper 
{
    Lending toModel(LendingEntity entity);
    LendingEntity toEntity(Lending model);
}