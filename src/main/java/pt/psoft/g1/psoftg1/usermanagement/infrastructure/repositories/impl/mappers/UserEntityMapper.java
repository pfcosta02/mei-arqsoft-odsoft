package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.LibrarianEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserEntityMapper
{
    User toModel(UserEntity entity);
    UserEntity toEntity(User model);

    Librarian toModel(LibrarianEntity entity);
    @Mapping(target = "role", source="authorities")
    LibrarianEntity toEntity(Librarian user);

    Reader toModel(ReaderEntity entity);
    @Mapping(target = "role", source="authorities")
    ReaderEntity toEntity(Reader user);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "role", source="authorities")
    ReaderEntity toReaderEntity(UserEntity userEntity);

    default String map(NameEntity value)
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
}