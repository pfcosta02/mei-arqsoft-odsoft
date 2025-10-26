package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.NameMapperMongoDB;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoMapperMongoDB;


@Mapper(componentModel = "spring", uses = { NameMapperMongoDB.class, BioMapperMongoDB.class, PhotoMapperMongoDB.class})
public interface AuthorMapperMongoDB
{
    Author toModel(AuthorMongoDB entity);
    AuthorMongoDB toMongoDB(Author model);
}