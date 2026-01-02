package pt.psoft.g1.psoftg1.readermanagement.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.shared.api.MapperInterface;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ReaderViewAMQPMapper extends MapperInterface {


    @Named("stringToReaderNumber")
    protected ReaderNumber mapReaderNumber(String value) {
        return value == null ? null : new ReaderNumber(value);
    }

    @Named("stringToPhoneNumber")
    protected PhoneNumber mapPhoneNumber(String value) {
        return value == null ? null : new PhoneNumber(value);
    }

    @Named("stringToBirthDate")
    protected BirthDate mapBirthDate(String value) {
        return value == null ? null : new BirthDate(value);
    }

    @Mapping(target = "readerNumber", expression = "java(reader.getReaderNumber())")
    @Mapping(target = "readerUsername", source = "reader.reader.username")
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "gdprConsent", ignore = true)
    @Mapping(target = "marketingConsent", ignore = true)
    @Mapping(target = "thirdPartySharingConsent", ignore = true)
    @Mapping(target = "interestList", ignore = true)
    @Mapping(target = "version", source = "version")
    public abstract ReaderViewAMQP toReaderViewAMQP(ReaderDetails reader);

    public abstract List<ReaderViewAMQP> toReaderViewAMQP(List<ReaderDetails> readerList);

    @Mapping(
            target = "readerNumber",
            source = "readerNumber",
            qualifiedByName = "stringToReaderNumber"
    )
    @Mapping(
            target = "phoneNumber",
            source = "phoneNumber",
            qualifiedByName = "stringToPhoneNumber"
    )
    @Mapping(
            target = "birthDate",
            source = "birthDate",
            qualifiedByName = "stringToBirthDate"
    )
    @Mapping(target = "reader.username", source = "readerUsername")
    @Mapping(target = "version", source = "version")
    public abstract ReaderDetails toReaderDetails(ReaderViewAMQP readerViewAMQP);

    public abstract List<ReaderDetails> toReaderDetails(List<ReaderViewAMQP> readerViewAMQPList);

}
