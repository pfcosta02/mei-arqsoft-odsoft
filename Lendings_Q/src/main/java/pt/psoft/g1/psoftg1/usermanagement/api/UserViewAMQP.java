package pt.psoft.g1.psoftg1.usermanagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Schema(description = "A User form AMQP communication")
public class UserViewAMQP {
    private Long version;
    private String username;
    private String password;
    private String name;
    private Set<String> authorities = new HashSet<>();
}
