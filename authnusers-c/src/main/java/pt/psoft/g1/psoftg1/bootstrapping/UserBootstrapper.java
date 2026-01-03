package pt.psoft.g1.psoftg1.bootstrapping;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("bootstrap")
@Order(1)
public class UserBootstrapper implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final IdGenerator idGenerator;
    private List<String> queriesToExecute = new ArrayList<>();

    @Override
    @Transactional
    public void run(final String... args) {
        createAdmin();
        createReaders();
        createLibrarian();
        executeQueries();
    }

    private void createAdmin()
    {
        // Admin - João
        if (userRepository.findByUsername("admin@gmail.com").isEmpty()) {
            final User admin = User.newUser("admin@gmail.com", "Admin123!", "Administrator", Role.ADMIN);
            admin.setId(idGenerator.generateId());
            userRepository.save(admin);
        }
    }

    private void createReaders() {
        // Reader1 - Manuel
        if (userRepository.findByUsername("manuel@gmail.com").isEmpty()) {
            final User manuel = User.newUser("manuel@gmail.com", "Manuelino123!", "Manuel Sarapinto das Coives", Role.READER);
            manuel.setId(idGenerator.generateId());
            userRepository.save(manuel);
        }
    }

    private void createLibrarian() {
        // Maria
        if (userRepository.findByUsername("maria@gmail.com").isEmpty()) {
            final User maria = Librarian.newLibrarian("maria@gmail.com", "Mariaroberta!123", "Maria Roberta");
            maria.setId(idGenerator.generateId());
            userRepository.save(maria);
        }
        if( userRepository.findByUsername("luis@gmail.com").isEmpty()) {
            final User luis = Librarian.newLibrarian("luis@gmail.com", "Luispassword123!", "Luis Silva");
            luis.setId(idGenerator.generateId());
            userRepository.save(luis);
        }
    }

    private void executeQueries() {
        for (String query : queriesToExecute) {
            jdbcTemplate.update(query);
        }
    }
}
