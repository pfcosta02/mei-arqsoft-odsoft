package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;

import java.io.Serializable;

@Profile("mongodb")
@Primary
@EqualsAndHashCode
public class TitleMongoDB implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long TitleId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = Title.TITLE_MAX_LENGTH)
    @Column(name = "TITLE", length = Title.TITLE_MAX_LENGTH, nullable = false)
    @Getter
    private String title;

    protected TitleMongoDB() {}

    public TitleMongoDB(String title) {
        this.title = title;
    }
}

