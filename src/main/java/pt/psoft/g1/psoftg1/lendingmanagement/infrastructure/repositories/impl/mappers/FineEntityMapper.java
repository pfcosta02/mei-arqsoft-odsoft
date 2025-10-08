package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.FineEntity;

@Mapper(componentModel = "spring")
public interface FineEntityMapper
{
    Fine toModel(FineEntity entity);
    FineEntity toEntity(Fine model);
}