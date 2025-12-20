package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers;

import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.LibrarianEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-20T16:56:20+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class UserEntityMapperImpl implements UserEntityMapper {

    @Override
    public User toModel(UserEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String password = null;
        String username = null;

        password = entity.getPassword();
        username = entity.getUsername();

        User user = new User( username, password );

        user.setName( map( entity.getName() ) );
        user.setEnabled( entity.isEnabled() );
        if ( user.getAuthorities() != null ) {
            Set<Role> set = entity.getAuthorities();
            if ( set != null ) {
                user.getAuthorities().addAll( set );
            }
        }
        user.id = entity.getId();

        return user;
    }

    @Override
    public UserEntity toEntity(User model) {
        if ( model == null ) {
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setEnabled( model.isEnabled() );
        userEntity.setUsername( model.getUsername() );
        userEntity.setName( nameToNameEntity( model.getName() ) );
        if ( userEntity.getAuthorities() != null ) {
            Set<Role> set = model.getAuthorities();
            if ( set != null ) {
                userEntity.getAuthorities().addAll( set );
            }
        }

        return userEntity;
    }

    @Override
    public Librarian toModel(LibrarianEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String password = null;
        String username = null;

        password = entity.getPassword();
        username = entity.getUsername();

        Librarian librarian = new Librarian( username, password );

        librarian.setName( map( entity.getName() ) );
        librarian.setEnabled( entity.isEnabled() );
        if ( librarian.getAuthorities() != null ) {
            Set<Role> set = entity.getAuthorities();
            if ( set != null ) {
                librarian.getAuthorities().addAll( set );
            }
        }
        librarian.id = entity.getId();

        return librarian;
    }

    @Override
    public LibrarianEntity toEntity(Librarian user) {
        if ( user == null ) {
            return null;
        }

        Role role = null;
        String username = null;
        String password = null;

        role = map( user.getAuthorities() );
        username = user.getUsername();
        password = user.getPassword();

        LibrarianEntity librarianEntity = new LibrarianEntity( username, password, role );

        librarianEntity.setEnabled( user.isEnabled() );
        librarianEntity.setName( nameToNameEntity( user.getName() ) );
        if ( librarianEntity.getAuthorities() != null ) {
            Set<Role> set = user.getAuthorities();
            if ( set != null ) {
                librarianEntity.getAuthorities().addAll( set );
            }
        }

        return librarianEntity;
    }

    @Override
    public Reader toModel(ReaderEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String password = null;
        String username = null;

        password = entity.getPassword();
        username = entity.getUsername();

        Reader reader = new Reader( username, password );

        reader.setName( map( entity.getName() ) );
        reader.setEnabled( entity.isEnabled() );
        if ( reader.getAuthorities() != null ) {
            Set<Role> set = entity.getAuthorities();
            if ( set != null ) {
                reader.getAuthorities().addAll( set );
            }
        }
        reader.id = entity.getId();

        return reader;
    }

    @Override
    public ReaderEntity toEntity(Reader user) {
        if ( user == null ) {
            return null;
        }

        Role role = null;
        String username = null;
        String password = null;

        role = map( user.getAuthorities() );
        username = user.getUsername();
        password = user.getPassword();

        ReaderEntity readerEntity = new ReaderEntity( username, password, role );

        readerEntity.setEnabled( user.isEnabled() );
        readerEntity.setName( nameToNameEntity( user.getName() ) );
        if ( readerEntity.getAuthorities() != null ) {
            Set<Role> set = user.getAuthorities();
            if ( set != null ) {
                readerEntity.getAuthorities().addAll( set );
            }
        }

        return readerEntity;
    }

    @Override
    public ReaderEntity toReaderEntity(UserEntity userEntity) {
        if ( userEntity == null ) {
            return null;
        }

        Role role = null;
        String username = null;
        String password = null;

        role = map( userEntity.getAuthorities() );
        username = userEntity.getUsername();
        password = userEntity.getPassword();

        ReaderEntity readerEntity = new ReaderEntity( username, password, role );

        readerEntity.setEnabled( userEntity.isEnabled() );
        readerEntity.setName( userEntity.getName() );
        if ( readerEntity.getAuthorities() != null ) {
            Set<Role> set = userEntity.getAuthorities();
            if ( set != null ) {
                readerEntity.getAuthorities().addAll( set );
            }
        }

        return readerEntity;
    }

    protected NameEntity nameToNameEntity(Name name) {
        if ( name == null ) {
            return null;
        }

        String name1 = null;

        name1 = name.getName();

        NameEntity nameEntity = new NameEntity( name1 );

        return nameEntity;
    }
}
