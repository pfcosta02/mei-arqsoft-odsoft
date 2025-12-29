package pt.psoft.g1.psoftg1.genremanagement.api;

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
@Schema(description = "A Book form AMQP communication")
@NoArgsConstructor
public class GenreViewAMQP {

    @NotNull
    private String pk;

    @NotNull
    private String genre;

    public GenreViewAMQP(String genre) {
        this.genre = genre;
    }
}
