package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pt.psoft.g1.psoftg1.shared.model.relational.EntityWithPhotoEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Entity
@Table(name = "READER_DETAILS")
@Profile("jpa")
@Primary
public class ReaderDetailsEntity extends EntityWithPhotoEntity {
    @Id
    @Getter
    @Setter
    private String id;

    @Embedded
    @Getter
    @Setter
    @OneToOne
    private ReaderEntity reader;

    @Embedded
    @Setter
    @Getter
    private ReaderNumberEntity readerNumber;

    @Embedded
    @Getter
    @Setter
    private BirthDateEntity birthDate;

    @Embedded
    @Setter
    @Getter
    private PhoneNumberEntity phoneNumber;

    @Basic
    @Getter
    @Setter
    private boolean gdprConsent;

    @Getter
    @Setter
    @Basic
    private boolean marketingConsent;

    @Getter
    @Setter
    @Basic
    private boolean thirdPartySharingConsent;

    @Setter
    @Getter
    @Version
    private long version;

    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(name = "reader_interests", joinColumns = @JoinColumn(name = "reader_id"))
    @Column(name = "genre_id")
    private List<String> interestList;

    protected ReaderDetailsEntity() {}

    public ReaderDetailsEntity(ReaderNumberEntity readerNumber, ReaderEntity reader, BirthDateEntity birthDate, PhoneNumberEntity phoneNumber,
                               boolean gdprConsent, boolean marketingConsent, boolean thirdPartySharingConsent,
                               PhotoEntity photo, List<String> interestList)
    {
        setReader(reader);
        setReaderNumber(readerNumber);
        setPhoneNumber(phoneNumber);
        setBirthDate(birthDate);
        setGdprConsent(gdprConsent);
        setPhoto(photo);
        setMarketingConsent(marketingConsent);
        setThirdPartySharingConsent(thirdPartySharingConsent);
        setInterestList(interestList);
    }
}
