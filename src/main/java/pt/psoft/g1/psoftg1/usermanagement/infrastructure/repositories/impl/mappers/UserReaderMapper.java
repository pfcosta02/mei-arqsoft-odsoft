package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;

@Mapper(componentModel = "spring")
public interface UserReaderMapper {

    /**
     * Transforma um User em Reader.
     * Mantém username, password, name, enabled e roles, garantindo ROLE_READER.
     *
     * @param user User original
     * @return Reader equivalente
     */
    public static Reader toReader(User user) {
        if (user == null) {
            return null;
        }

        // Cria Reader com username e password já encriptado
        Reader reader = new Reader(user.getUsername(), user.getPassword());
        reader.setName(user.getName().toString());
        reader.setEnabled(user.isEnabled());

        // Copia roles existentes
        user.getAuthorities().forEach(reader::addAuthority);

        // Garante que ROLE_READER esteja presente
        reader.addAuthority(new Role(Role.READER));

        return reader;
    }
}