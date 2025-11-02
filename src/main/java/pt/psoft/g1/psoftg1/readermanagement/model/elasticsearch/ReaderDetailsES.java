package pt.psoft.g1.psoftg1.readermanagement.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pt.psoft.g1.psoftg1.genremanagement.model.elasticsearch.GenreES;
import pt.psoft.g1.psoftg1.readermanagement.services.UpdateReaderRequest;
import pt.psoft.g1.psoftg1.shared.model.elasticsearch.EntityWithPhotoES;
import pt.psoft.g1.psoftg1.usermanagement.model.elasticsearch.ReaderES;

import java.util.List;


@Document(indexName = "reader_details")
@JsonIgnoreProperties(ignoreUnknown = true)  // ✅ Ignora campos desconhecidos
@Getter
@Setter
public class ReaderDetailsES extends EntityWithPhotoES {

    @Id
    private String id;

    @Field(type = FieldType.Object)
    private ReaderES reader;

    @Field(type = FieldType.Keyword)
    private String readerNumber;

    @Field(type = FieldType.Object)
    private BirthDateES birthDate;

    @Field(type = FieldType.Keyword)
    private String phoneNumber;

    @Field(type = FieldType.Boolean)
    private boolean gdprConsent;

    @Field(type = FieldType.Boolean)
    private boolean marketingConsent;

    @Field(type = FieldType.Boolean)
    private boolean thirdPartySharingConsent;

    @Field(type = FieldType.Nested)
    private List<GenreES> interestList;

    public ReaderDetailsES() {}

    public ReaderDetailsES(String readerNumber, ReaderES reader, String birthDate, String phoneNumber,
                           boolean gdpr, boolean marketing, boolean thirdParty,
                           String photoURI, List<GenreES> interestList) {

        if(reader == null || phoneNumber == null) {
            throw new IllegalArgumentException("Provided argument resolves to null object");
        }

        if(!gdpr) {
            throw new IllegalArgumentException("Readers must agree with the GDPR rules");
        }

        this.reader = reader;
        this.readerNumber = readerNumber;
        this.phoneNumber = phoneNumber;
        this.birthDate = new BirthDateES(birthDate);
        this.gdprConsent = true;
        this.marketingConsent = marketing;
        this.thirdPartySharingConsent = thirdParty;
        this.setPhotoInternal(photoURI);
        this.interestList = interestList;
    }

    public void applyPatch(UpdateReaderRequest request, String photoURI, List<GenreES> interestList) {
        if(request.getUsername() != null) reader.setUsername(request.getUsername());
        if(request.getPassword() != null) reader.setPassword(request.getPassword());
        if(request.getFullName() != null) reader.setName(request.getFullName());
        if(request.getBirthDate() != null) birthDate = new BirthDateES(request.getBirthDate());
        if(request.getPhoneNumber() != null) phoneNumber = request.getPhoneNumber();  // ✅ String direto
        marketingConsent = request.getMarketing();
        thirdPartySharingConsent = request.getThirdParty();
        if(photoURI != null) setPhotoInternal(photoURI);
        if(interestList != null) this.interestList = interestList;
    }

}