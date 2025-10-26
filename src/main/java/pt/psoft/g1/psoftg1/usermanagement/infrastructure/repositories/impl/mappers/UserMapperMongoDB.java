package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.LibrarianMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;

import java.util.Set;


@Mapper(componentModel = "spring")
public interface UserMapperMongoDB
{
    User toModel(UserMongoDB entity);
    UserMongoDB toEntity(User model);

    Librarian toModel(LibrarianMongoDB entity);
    @Mapping(target = "role", source="authorities")
    LibrarianMongoDB toEntity(Librarian user);

    Reader toModel(ReaderMongoDB entity);
    @Mapping(target = "role", source="authorities")
    ReaderMongoDB toEntity(Reader user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "role", source="authorities")
    ReaderMongoDB toReaderMongoDB(UserMongoDB userMongoDB);

    default String map(NameMongoDB value)
    {
        return value == null ? null : value.toString();
    }

    // TODO: Confirmar com o professor "Apesar de ter um Set<Role> cada um User so pode ter uma ROLE, certo?"
    default Role map(Set<Role> value)
    {
        if (value == null || value.isEmpty())
        {
            return null;
        }
        return value.iterator().next(); // Take the first role
    }

    default Long map(String value) {
        if (value == null) return null;
        return Long.parseLong(value);
    }

    default String map(Long value) {
        if (value == null) return null;
        return value.toString();
    }
}