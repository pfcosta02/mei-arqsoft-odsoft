package pt.psoft.g1.psoftg1.authormanagement.model.elasticsearch;

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
import pt.psoft.g1.psoftg1.authormanagement.services.UpdateAuthorRequest;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.EntityWithPhotoES;

@Document(indexName = "authors")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthorES extends EntityWithPhotoES {

    @Id
    private String authorNumber;

    @Version
    private Long version;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String bio;

    public void applyPatch(final long desiredVersion, final UpdateAuthorRequest request) {
        if (this.version != null && !this.version.equals(desiredVersion)) {
            throw new ConflictException("Object was already modified by another user");
        }

        if (request.getName() != null)
            this.name = request.getName();

        if (request.getBio() != null)
            this.bio = request.getBio();

        if (request.getPhotoURI() != null)
            setPhotoInternal(request.getPhotoURI());
    }

    public void removePhoto(long desiredVersion) {
        if (this.version != null && !this.version.equals(desiredVersion)) {
            throw new ConflictException("Provided version does not match latest version of this object");
        }
        setPhotoInternal(null);
    }

}