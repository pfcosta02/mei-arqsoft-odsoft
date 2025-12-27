package pt.psoft.g1.psoftg1.readermanagement.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pt.psoft.g1.psoftg1.readermanagement.model.*;

import java.util.List;

/**
 * Brief guide:
 * <a href="https://www.baeldung.com/mapstruct">https://www.baeldung.com/mapstruct</a>
 * */
@Mapper(componentModel = "spring", uses = {ReaderService.class})
public abstract class ReaderMapper {

    @Mapping(target = "gdprConsent", source = "request.gdpr")
    @Mapping(target = "marketingConsent", source = "request.marketing")
    @Mapping(target = "thirdPartySharingConsent", source = "request.thirdParty")
    @Mapping(target = "reader", source = "reader")
    @Mapping(target = "photo", source = "photo")
    @Mapping(target = "interestList", source = "interestList")
    @Mapping(target = "id", ignore = true)
    public abstract ReaderDetails createReaderDetails(int readerNumber, Reader reader, CreateReaderRequest request, String photo, List<String> interestList);

    ReaderNumber mapReaderNumber(int value) {
        return new ReaderNumber(value);
    }

    BirthDate mapBirthDate(String value) {
        return new BirthDate(value);
    }

    PhoneNumber mapPhoneNumber(String value) {
        return new PhoneNumber(value);
    }
}