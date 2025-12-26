package pt.psoft.g1.psoftg1.readermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserDTO {

    public final String id;
    public final String readerId;
    public final String username;
    public final String password;
    public final String fullname;
    public final Long version;

    public UserDTO(@JsonProperty("id") String id,
                   @JsonProperty("readerId") String readerId,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("fullname") String fullname,
                   @JsonProperty("version") Long version)
    {
        this.id = id;
        this.readerId = readerId;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.version = version;
    }
}