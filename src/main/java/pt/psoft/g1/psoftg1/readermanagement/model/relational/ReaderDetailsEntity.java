package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.shared.model.relational.EntityWithPhotoEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Table(name = "READER_DETAILS")
@Profile("jpa")
@Primary
public class ReaderDetailsEntity extends EntityWithPhotoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @OneToOne
    @Getter
    @Setter
    private ReaderEntity reader;

    @Embedded
    @Getter
    @Setter
    private ReaderNumberEntity readerNumber;

    @Embedded
    @Getter
    @Setter
    private BirthDateEntity birthDate;

    @Embedded
    @Getter
    @Setter
    private PhoneNumberEntity phoneNumber;

    @Basic
    @Getter
    @Setter
    private boolean gdprConsent;

    @Basic
    @Getter
    @Setter
    private boolean marketingConsent;

    @Basic
    @Getter
    @Setter
    private boolean thirdPartySharingConsent;

    @Version
    @Getter
    @Setter
    private Long version;

    @ManyToMany
    @Getter
    @Setter
    private List<GenreEntity> interestList;

    protected ReaderDetailsEntity() {}

    public ReaderDetailsEntity(ReaderNumberEntity readerNumber, ReaderEntity reader, BirthDateEntity birthDate, PhoneNumberEntity phoneNumber,
                               boolean gdpr, boolean marketing, boolean thirdParty,
                               PhotoEntity photoURI, List<GenreEntity> interestList)
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
