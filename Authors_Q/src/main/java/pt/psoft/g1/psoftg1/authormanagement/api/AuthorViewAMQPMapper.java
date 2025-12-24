package pt.psoft.g1.psoftg1.authormanagement.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;

@Mapper(componentModel = "spring")
public abstract class AuthorViewAMQPMapper extends MapperInterface {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "bio", source = "bio")
    @Mapping(target = "version", expression = "java(author.getVersion())")

    public abstract AuthorViewAMQP toAuthorViewAMQP(Author author);
}
