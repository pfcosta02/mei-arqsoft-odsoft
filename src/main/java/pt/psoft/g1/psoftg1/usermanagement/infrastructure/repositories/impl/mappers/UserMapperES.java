package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.*;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.NameES;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch.UserES;
import pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch.RoleES;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapperES {

    UserES toElasticsearch(User user);

    User toEntity(UserES userES);

    // --- Helpers ---
    default String longToString(Long id) {
        return id != null ? id.toString() : null;
    }

    default NameES nameToNameES(Name name) {
        return name != null ? new NameES(name.getName()) : null;
    }

    default String nameToString(Name name) {
        return name != null ? name.getName() : null;
    }

    default String nameESToString(NameES name) {
        return name != null ? name.getName() : null;
    }

    default Name nameEStoName(NameES nameES) {
        return nameES != null ? new Name(nameES.getName()) : null;
    }

    default Set<RoleES> rolesToRolesES(Set<Role> roles) {
        return roles != null
                ? roles.stream().map(r -> new RoleES(r.getAuthority())).collect(Collectors.toSet())
                : null;
    }

    default Set<Role> rolesESToRoles(Set<RoleES> rolesES) {
        return rolesES != null
                ? rolesES.stream().map(r -> new Role(r.getAuthority())).collect(Collectors.toSet())
                : null;
    }
}
