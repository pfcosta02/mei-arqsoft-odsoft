package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.relational.ForbiddenNameEntity;

@Mapper(componentModel = "spring")
public interface ForbiddenNameEntityMapper
{
    ForbiddenName toModel(ForbiddenNameEntity entity);
    ForbiddenNameEntity toEntity(ForbiddenName model);


}