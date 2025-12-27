package pt.psoft.g1.psoftg1.usermanagement.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class RoleDTO {

    public final String authority;

    public RoleDTO(@JsonProperty("authority") String authority) {
        this.authority = authority;
    }
}