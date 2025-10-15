package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

import java.nio.file.Paths;

@Mapper(componentModel = "spring")
public interface PhotoEntityMapper
{
    default Photo toModel(PhotoEntity entity)
    {
        return entity == null ? null : new Photo(Paths.get(entity.getPhotoFile()));
    }

    default PhotoEntity toEntity(Photo model)
    {
        return model == null ? null : new PhotoEntity(model.getPhotoFile().toString());
    }

    default String map(PhotoEntity value)
    {
        return value == null ? null : value.getPhotoFile();
    }
}
