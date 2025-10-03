package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

@Mapper(componentModel = "spring")
public interface PhotoEntityMapper
{
    Photo toModel(PhotoEntity entity);
    PhotoEntity toEntity(Photo model);
}
