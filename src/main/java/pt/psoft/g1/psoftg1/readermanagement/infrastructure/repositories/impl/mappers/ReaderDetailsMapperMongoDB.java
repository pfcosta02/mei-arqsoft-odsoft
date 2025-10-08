package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoDB;

@Mapper(componentModel = "spring")
public interface ReaderDetailsMapperMongoDB
{
    ReaderDetails toModel(ReaderDetailsMongoDB entity);
    ReaderDetailsMongoDB toEntity(ReaderDetails model);
}