package pt.psoft.g1.psoftg1.authormanagement.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "A Author form AMQP communication")
@NoArgsConstructor
public class AuthorViewAMQP {

    @NotNull
    private String authorNumber;

    @NotNull
    private String name;

    @NotNull
    private String bio;

    @NotNull
    private long version;

    public AuthorViewAMQP(String name, String bio) {
        this.name = name;
        this.bio = bio;
    }
}
