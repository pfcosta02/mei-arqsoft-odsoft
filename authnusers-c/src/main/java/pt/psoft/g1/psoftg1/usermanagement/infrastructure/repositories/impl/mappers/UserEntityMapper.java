package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.NameEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.LibrarianEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserTempEntity;

import java.util.Set;

@Mapper(componentModel = "spring", uses = { NameEntityMapper.class})
public interface UserEntityMapper
{
    User toModel(UserEntity entity);
    UserEntity toEntity(User model);

    Librarian toModel(LibrarianEntity entity);
    @Mapping(target = "role", source="authorities")
    LibrarianEntity toEntity(Librarian user);

    User toModelFromTemp(UserTempEntity tempEntity);

    UserTempEntity toTempEntity(User model);

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