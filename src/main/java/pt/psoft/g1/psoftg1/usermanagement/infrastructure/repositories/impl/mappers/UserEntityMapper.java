package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.LibrarianEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;

@Mapper(componentModel = "spring")
public interface UserEntityMapper
{
    User toModel(UserEntity entity);
    UserEntity toEntity(User model);

    Librarian toModel(LibrarianEntity entity);
    LibrarianEntity toEntity(Librarian user);

    Reader toModel(ReaderEntity entity);
    ReaderEntity toEntity(Reader user);

    default String map(NameEntity value)
    {
        return value == null ? null : value.toString();
    }
}