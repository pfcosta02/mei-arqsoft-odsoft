package pt.psoft.g1.psoftg1.shared.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnRequest {
    @Size(max = 1024)
    private String commentary;

    @Min(0) @Max(10)
    private Integer rating;

    // getters/setters
}

