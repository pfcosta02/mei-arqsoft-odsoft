package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.BioEntity;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

@Mapper(componentModel = "spring")
public interface AuthorEntityMapper
{
    Author toModel(AuthorEntity entity);
    AuthorEntity toEntity(Author model);

    default String map(Photo photo)
    {
        return photo == null ? null : photo.getPhotoFile();
    }

    default String map(PhotoEntity photo)
    {
        return photo == null ? null : photo.getPhotoFile();
    }

    default String map(Name value)
    {
        return value == null ? null : value.getName();
    }

    default String map(NameEntity value)
    {
        return value == null ? null : value.getName();
    }

    default String map(Bio value)
    {
        return value == null ? null : value.getValue();
    }

    default String map(BioEntity value)
    {
        return value == null ? null : value.getBio();
    }

}