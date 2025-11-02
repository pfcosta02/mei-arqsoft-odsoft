package pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@NoArgsConstructor
public class TitleES {

    private static final int TITLE_MAX_LENGTH = 128;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = TITLE_MAX_LENGTH)
    @Field(type = FieldType.Text) // Pode usar Keyword se precisares de exata correspondência
    @Getter
    @Setter
    private String title;

    public TitleES(String title) {
        setTitle(title);
    }

    public void setTitle(String title) {
        if (title == null)
            throw new IllegalArgumentException("Title cannot be null");
        if (title.isBlank())
            throw new IllegalArgumentException("Title cannot be blank");
        if (title.length() > TITLE_MAX_LENGTH)
            throw new IllegalArgumentException("Title has a maximum of " + TITLE_MAX_LENGTH + " characters");
        this.title = title.strip();
    }

    @Override
    public String toString() {
        return this.title;
    }
}

