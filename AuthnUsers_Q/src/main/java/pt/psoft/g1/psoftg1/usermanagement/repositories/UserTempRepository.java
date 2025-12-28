package pt.psoft.g1.psoftg1.usermanagement.repositories;

import java.util.Optional;

import pt.psoft.g1.psoftg1.usermanagement.model.User;

public interface UserTempRepository 
{
    User save(User user);
    Optional<User> findByUserId(String userId);
    Optional<User> findByUsername(String username);
    void delete(String userId);
}
