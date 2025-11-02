package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.shared.model.Photo;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

@Mapper(componentModel = "spring")
public interface PhotoMapperES {

    default String photoToString(Photo photo) {
        return (photo != null) ? photo.getPhotoFile() : null;
    }

    default Photo photoFromString(String photoURI) {
        if (photoURI == null) return null;
        try {
            return new Photo(Path.of(photoURI));
        } catch (InvalidPathException e) {
            return null;
        }
    }
}