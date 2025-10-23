package pt.psoft.g1.psoftg1.readermanagement.model;

import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.readermanagement.services.UpdateReaderRequest;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;

import java.nio.file.InvalidPathException;
import java.util.List;

public class ReaderDetails extends EntityWithPhoto {

    // TODO: Substituir por ID e nao é suposto ser public
    public String readerDetailsId;

    private Reader reader;
    private ReaderNumber readerNumber;
    private BirthDate birthDate;
    private PhoneNumber phoneNumber;
    private boolean gdprConsent;
    private boolean marketingConsent;
    private boolean thirdPartySharingConsent;
    private Long version;
    private List<Genre> interestList;

    public ReaderDetails() {}

    // Construtor principal
    public ReaderDetails(ReaderNumber readerNumber, Reader reader, BirthDate birthDate, PhoneNumber phoneNumber,
                         boolean gdprConsent, boolean marketingConsent, boolean thirdPartySharingConsent,
                         String photo, List<Genre> interestList)
    {
        if (reader == null || phoneNumber == null)
        {
            throw new IllegalArgumentException("Provided argument resolves to null object");
        }

        if (!gdprConsent)
        {
            throw new IllegalArgumentException("Readers must agree with the GDPR rules");
        }

        setReader(reader);
        setReaderNumber(readerNumber);
        setPhoneNumber(phoneNumber);
        setBirthDate(birthDate);
        setGdprConsent(gdprConsent);
        setPhotoInternal(photo);
        setMarketingConsent(marketingConsent);
        setThirdPartySharingConsent(thirdPartySharingConsent);
        setInterestList(interestList);
    }

    public ReaderDetails(int readerNumber, Reader reader, String birthDate, String phoneNumber,
                         boolean gdprConsent, boolean marketingConsent, boolean thirdPartySharingConsent,
                         String photo, List<Genre> interestList)
    {
        this(new ReaderNumber(readerNumber), reader, new BirthDate(birthDate), new PhoneNumber(phoneNumber), gdprConsent, marketingConsent, thirdPartySharingConsent, photo, interestList);
    }

    // Getters and Setters
    public String getReaderDetailsId() { return readerDetailsId; }

    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }

    public String getReaderNumber() { return readerNumber.getReaderNumber(); }
    public void setReaderNumber(ReaderNumber readerNumber) {
        if(readerNumber != null) {
            this.readerNumber = readerNumber;
        }
    }

    public void setVersion(Long version) { this.version = version; }
    public Long getVersion() { return version; }

    public BirthDate getBirthDate() { return birthDate; }
    public void setBirthDate(BirthDate date) {
        if(date != null) {
            this.birthDate = date;
        }
    }

    public String getPhoneNumber() { return phoneNumber.toString(); }
    public void setPhoneNumber(PhoneNumber number) {
        if(number != null) {
            this.phoneNumber = number;
        }
    }
    public boolean isGdprConsent() { return gdprConsent; }
    public void setGdprConsent(boolean gdprConsent) { this.gdprConsent = gdprConsent; }

    public boolean isMarketingConsent() { return marketingConsent; }
    public void setMarketingConsent(boolean marketingConsent) { this.marketingConsent = marketingConsent; }

    public boolean isThirdPartySharingConsent() { return thirdPartySharingConsent; }
    public void setThirdPartySharingConsent(boolean thirdPartySharingConsent) { this.thirdPartySharingConsent = thirdPartySharingConsent; }

    public List<Genre> getInterestList() { return interestList; }
    public void setInterestList(List<Genre> interestList) { this.interestList = interestList; }

    // Método de patch
    public void applyPatch(long currentVersion, UpdateReaderRequest request, String photoURI, List<Genre> interestList)
    {
        if (currentVersion != this.version)
        {
            throw new ConflictException("Provided version does not match latest version of this object");
        }

        if (request.getUsername() != null)
        {
            reader.setUsername(request.getUsername());
        }
        if (request.getPassword() != null)
        {
            reader.setPassword(request.getPassword());
        }
        if (request.getFullName() != null)
        {
            reader.setName(request.getFullName());
        }
        if (request.getBirthDate() != null)
        {
            birthDate = new BirthDate(request.getBirthDate());
        }
        if (request.getPhoneNumber() != null)
        {
            phoneNumber = new PhoneNumber(request.getPhoneNumber());
        }
        this.marketingConsent = request.getMarketing();
        this.thirdPartySharingConsent = request.getThirdParty();

        if (photoURI != null)
        {
            try
            {
                setPhotoInternal(photoURI);
            }
            catch (InvalidPathException ignored)
            {

            }
        }

        if (interestList != null)
        {
            this.interestList = interestList;
        }
    }

    public void removePhoto(long desiredVersion)
    {
        if(desiredVersion != this.version)
        {
            throw new ConflictException("Provided version does not match latest version of this object");
        }

        setPhotoInternal((String)null);
    }
}