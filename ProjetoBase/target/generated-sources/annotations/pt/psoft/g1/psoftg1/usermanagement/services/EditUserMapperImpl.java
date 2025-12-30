package pt.psoft.g1.psoftg1.usermanagement.services;

import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-30T16:59:33+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class EditUserMapperImpl extends EditUserMapper {

    @Override
    public User create(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        String password = null;
        String username = null;

        password = map( request.getPassword() );
        username = map( request.getUsername() );

        User user = new User( username, password );

        if ( user.getAuthorities() != null ) {
            Set<Role> set = stringToRole( request.getAuthorities() );
            if ( set != null ) {
                user.getAuthorities().addAll( set );
            }
        }
        user.setName( map( request.getName() ) );

        return user;
    }

    @Override
    public void update(EditUserRequest request, User user) {
        if ( request == null ) {
            return;
        }

        if ( user.getAuthorities() != null ) {
            user.getAuthorities().clear();
            Set<Role> set = stringToRole( request.getAuthorities() );
            if ( set != null ) {
                user.getAuthorities().addAll( set );
            }
        }
        if ( request.getPassword() != null ) {
            user.setPassword( map( request.getPassword() ) );
        }
        if ( request.getUsername() != null ) {
            user.setUsername( map( request.getUsername() ) );
        }
        if ( request.getName() != null ) {
            user.setName( map( request.getName() ) );
        }
    }
}
