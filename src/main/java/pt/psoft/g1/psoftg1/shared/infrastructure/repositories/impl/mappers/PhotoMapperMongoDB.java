package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoDB;

@Mapper(componentModel = "spring")
public interface PhotoMapperMongoDB
{
    Photo toModel(PhotoMongoDB entity);
    PhotoMongoDB toMongoDB(Photo model);
}
