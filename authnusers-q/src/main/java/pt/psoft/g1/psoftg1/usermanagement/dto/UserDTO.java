package pt.psoft.g1.psoftg1.usermanagement.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.dto.RoleDTO;

@Data
public class UserDTO {
    public final String id;
    public final Long version;
    public final boolean enabled;
    public final String username;
    public final String password;
    public final String fullname;
    public final Set<RoleDTO> authorities;

    public UserDTO(@JsonProperty("id") String id,
                   @JsonProperty("username") String username,
                   @JsonProperty("password") String password,
                   @JsonProperty("name") String fullname,
                   @JsonProperty("version") Long version,
                   @JsonProperty("enabled") boolean enabled,
                   @JsonProperty("authorities") Set<RoleDTO> authorities)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.version = version;
        this.enabled = enabled;
        this.authorities = authorities;
    }
}