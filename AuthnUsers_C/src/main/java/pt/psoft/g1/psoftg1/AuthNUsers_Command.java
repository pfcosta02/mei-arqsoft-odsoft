package pt.psoft.g1.psoftg1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthNUsers_Command {

    public static void main(String[] args) {
        SpringApplication.run(AuthNUsers_Command.class, args);
    }

}
