package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;

@Mapper(componentModel = "spring")
public interface ReaderDetailsEntityMapper
{
    ReaderDetails toModel(ReaderDetailsEntity entity);
    ReaderDetailsEntity toEntity(ReaderDetails model);
}
