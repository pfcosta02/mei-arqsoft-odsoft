package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.LibrarianMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoDB;


@Mapper(componentModel = "spring")
public interface UserMapperMongoDB
{
    User toModel(UserMongoDB entity);
    UserMongoDB toEntity(User model);

    Librarian toModel(LibrarianMongoDB entity);
    LibrarianMongoDB toEntity(Librarian user);

    Reader toModel(ReaderMongoDB entity);
    ReaderMongoDB toEntity(Reader user);

    default String map(NameEntity value)
    {
        return value == null ? null : value.toString();
    }
}