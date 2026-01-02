package pt.psoft.g1.psoftg1.readermanagement.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class ReaderDTO {
    public final String readerId;
    public final String userId;
    public final NameDTO name;
    public final String email;

    public ReaderDTO(@JsonProperty("readerId") String readerId,
                     @JsonProperty("userId") String userId,
                     @JsonProperty("name") NameDTO name,
                     @JsonProperty("email") String email)
    {
        this.readerId = readerId;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }
}