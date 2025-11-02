package pt.psoft.g1.psoftg1.shared.model.elasticsearch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
public abstract class EntityWithPhotoES {

    @Field(type = FieldType.Text)
    protected String photo;

    protected void setPhotoInternal(String photoURI) {
        this.photo = photoURI;
    }

    public String getPhotoFile() {
        return photo;
    }
}