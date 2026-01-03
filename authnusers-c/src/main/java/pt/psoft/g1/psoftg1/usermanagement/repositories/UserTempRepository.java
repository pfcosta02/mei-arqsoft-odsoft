package pt.psoft.g1.psoftg1.usermanagement.repositories;

import pt.psoft.g1.psoftg1.usermanagement.model.User;

import java.util.Optional;

public interface UserTempRepository {
    User save(User user);
    Optional<User> findByUserId(String userId);
    Optional<User> findByUsername(String username);
    void delete(String userId);
}