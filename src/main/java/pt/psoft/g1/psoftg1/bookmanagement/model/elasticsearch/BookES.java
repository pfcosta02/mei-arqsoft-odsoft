package pt.psoft.g1.psoftg1.bookmanagement.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pt.psoft.g1.psoftg1.authormanagement.model.elasticsearch.AuthorES;
import pt.psoft.g1.psoftg1.genremanagement.model.elasticsearch.GenreES;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.EntityWithPhotoES;

import java.util.ArrayList;
import java.util.List;

@Document(indexName = "books")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookES extends EntityWithPhotoES {

    @Id
    private String id;  // Usaremos o ISBN como ID

    @Version
    private Long version;

    // Getters que retornam String
    @Field(type = FieldType.Text)
    private String isbn;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Object)
    private GenreES genre;

    @Field(type = FieldType.Nested)
    private List<AuthorES> authors = new ArrayList<>();

}