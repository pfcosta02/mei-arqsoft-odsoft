package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;

import java.io.Serializable;

@Profile("mongodb")
@EqualsAndHashCode
public class TitleMongoDB implements Serializable {

    @Id
    private String titleId;

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max = Title.TITLE_MAX_LENGTH)
    @Field("title")
    @Getter
    private String title;

    protected TitleMongoDB() {}

    public TitleMongoDB(String title) { this.title = title; }

    @Override
    public String toString() { return title; }
}