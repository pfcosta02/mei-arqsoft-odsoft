package pt.psoft.g1.psoftg1.readermanagement.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class NameDTO {
    public final String name;

    public NameDTO(@JsonProperty("name") String name)
    {
        this.name = name;
    }
}
