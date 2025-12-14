package pt.psoft.g1.psoftg1.readermanagement.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoDB;

import java.util.List;

@Document(collection = "reader_details")
// @EnableMongoAuditing
@Profile("mongodb")
public class ReaderDetailsMongoDB extends EntityWithPhotoMongoDB {

    @Id
    @Getter
    private String readerDetailsId;

    @Getter @Setter
    private ReaderMongoDB reader;

    @Getter @Setter
    private ReaderNumberMongoDB readerNumber;

    @Getter @Setter
    private BirthDateMongoDB birthDate;

    @Getter @Setter
    private PhoneNumberMongoDB phoneNumber;

    @Getter @Setter
    private boolean gdprConsent;

    @Getter @Setter
    private boolean marketingConsent;

    @Getter @Setter
    private boolean thirdPartySharingConsent;

    @Version
    @Getter @Setter
    private Long version;

    @Getter @Setter
    private List<GenreMongoDB> interestList;

    protected ReaderDetailsMongoDB() {}

    public ReaderDetailsMongoDB(ReaderNumberMongoDB readerNumber, ReaderMongoDB reader, BirthDateMongoDB birthDate, PhoneNumberMongoDB phoneNumber,
                                boolean gdpr, boolean marketing, boolean thirdParty,
                                PhotoMongoDB photoURI, List<GenreMongoDB> interestList)
    {
        setReaderNumber(readerNumber);
        setReader(reader);
        setBirthDate(birthDate);
        setPhoneNumber(phoneNumber);
        setGdprConsent(gdpr);
        setMarketingConsent(marketing);
        setThirdPartySharingConsent(thirdParty);
        setPhoto(photoURI);
        setInterestList(interestList);
    }
}