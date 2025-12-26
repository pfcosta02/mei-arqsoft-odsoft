package pt.psoft.g1.psoftg1.readermanagement.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import pt.psoft.g1.psoftg1.shared.model.relational.EntityWithPhotoEntity;

@Profile("jpa")
@Primary
@Entity
@Table(name = "READER_DETAILS_TEMP")
public class ReaderDetailsTempEntity extends EntityWithPhotoEntity
{
    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    @OneToOne
    private ReaderTempEntity reader;

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

    @Getter
    @Setter
    @Version
    private long version;

    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(name = "reader_temp_interests", joinColumns = @JoinColumn(name = "reader_id"))
    @Column(name = "genre_id")
    private List<String> interestList;
}