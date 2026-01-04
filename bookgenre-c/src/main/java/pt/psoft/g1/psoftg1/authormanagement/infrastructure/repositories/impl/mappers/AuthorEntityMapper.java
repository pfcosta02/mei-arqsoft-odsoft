package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.NameEntityMapper;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoEntityMapper;

@Mapper(componentModel = "spring", uses = { NameEntityMapper.class, BioEntityMapper.class, PhotoEntityMapper.class})
public interface AuthorEntityMapper
{
    @Mapping(target = "authorNumber", source = "authorNumber")
    Author toModel(AuthorEntity entity);
    @Mapping(target = "authorNumber", source = "authorNumber")
    AuthorEntity toEntity(Author model);
}