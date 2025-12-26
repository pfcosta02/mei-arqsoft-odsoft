package pt.psoft.g1.psoftg1.readermanagement.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class BirthDateDTO
{
    public final String birthDate;

    public BirthDateDTO(@JsonProperty("birthDate") String birthDate)
    {
        this.birthDate = birthDate;
    }
}