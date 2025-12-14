package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.shared.model.Photo;


@Mapper(componentModel = "spring")
public interface AuthorMapperMongoDB {

    AuthorMapperMongoDB INSTANCE = Mappers.getMapper( AuthorMapperMongoDB.class );

    @Mappings({
            @Mapping(target = "authorNumber", source = "authorNumber")// Ignore the MongoDB ID for the domain model
    })
    AuthorMongoDB toMongoDB(Author author);

    Author toModel(AuthorMongoDB authorMongoDB);

    String map(Photo value);
}

