package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoDB;
import java.nio.file.Paths;

@Mapper(componentModel = "spring")
public interface PhotoMapperMongoDB {

    default Photo toModel(PhotoMongoDB entity) {
        return entity == null ? null : new Photo(Paths.get(entity.getPhotoFile()));
    }

    default PhotoMongoDB toEntity(Photo model) {
        return model == null ? null : new PhotoMongoDB(model.getPhotoFile().toString());
    }

    default String map(PhotoMongoDB entity) {
        return entity == null ? null : entity.getPhotoFile();
    }
}