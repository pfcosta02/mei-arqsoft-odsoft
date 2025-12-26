package pt.psoft.g1.psoftg1.readermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;

import java.util.List;

@Data
public class ReaderDetailsDTO {

    public final String id;
    public final ReaderDTO reader;
    public final ReaderNumber readerNumber;
    public final BirthDateDTO birthDate;
    public final PhoneNumber phoneNumber;
    public final boolean gdprConsent;
    public final boolean marketingConsent;
    public final boolean thirdPartySharingConsent;
    public final long version;
    public final List<String> interestList;
    public final String photo;

    public ReaderDetailsDTO(@JsonProperty("id") String id,
                            @JsonProperty("reader") ReaderDTO reader,
                            @JsonProperty("readerNumber") ReaderNumber readerNumber,
                            @JsonProperty("birthDate") BirthDateDTO birthDate,
                            @JsonProperty("phoneNumber") PhoneNumber phoneNumber,
                            @JsonProperty("gdprConsent") boolean gdprConsent,
                            @JsonProperty("marketingConsent") boolean marketingConsent,
                            @JsonProperty("thirdPartySharingConsent") boolean thirdPartySharingConsent,
                            @JsonProperty("version") long version,
                            @JsonProperty("interestList") List<String> interestList,
                            @JsonProperty("photo") String photo)
    {
        this.id = id;
        this.reader = reader;
        this.readerNumber = readerNumber;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.gdprConsent = gdprConsent;
        this.marketingConsent = marketingConsent;
        this.thirdPartySharingConsent = thirdPartySharingConsent;
        this.version = version;
        this.interestList = interestList;
        this.photo = photo;
    }
}