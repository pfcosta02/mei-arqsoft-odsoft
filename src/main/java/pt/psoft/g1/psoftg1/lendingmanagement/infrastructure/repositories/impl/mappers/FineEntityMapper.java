package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.FineEntity;

@Mapper(componentModel = "spring", uses = { LendingEntityMapper.class})
public interface FineEntityMapper
{
    Fine toModel(FineEntity entity);
    FineEntity toEntity(Fine model);

    default String map(Bio value)
    {
        return value == null ? null : value.getValue();
    }
}