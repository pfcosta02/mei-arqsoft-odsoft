package pt.psoft.g1.psoftg1.readermanagement.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsMapperMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.util.List;

/**
 * Brief guide:
 * <a href="https://www.baeldung.com/mapstruct">https://www.baeldung.com/mapstruct</a>
 * */

@Mapper(componentModel = "spring", uses = {ReaderService.class, UserService.class, ReaderDetailsMapperMongoDB.class})
//@Mapper(componentModel = "spring", uses = {ReaderService.class, UserService.class})     ReaderDetailsEntityMapper.class
public abstract class ReaderMapper {

    @Mapping(target = "username", source = "username")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "name", source = "fullName")
    public abstract Reader createReader(CreateReaderRequest request);

    @Mapping(target = "gdprConsent", source = "request.gdpr")
    @Mapping(target = "marketingConsent", source = "request.marketing")
    @Mapping(target = "thirdPartySharingConsent", source = "request.thirdParty")
    @Mapping(target = "birthDate", source = "request.birthDate", qualifiedByName = "toBirthDate")
    @Mapping(target = "phoneNumber", source = "request.phoneNumber", qualifiedByName = "toPhoneNumber")
    @Mapping(target = "photo", source = "photo")
    @Mapping(target = "interestList", source = "interestList")
    @Mapping(target = "readerDetailsId", ignore = true)
    public abstract ReaderDetails createReaderDetails(int readerNumber, Reader reader, CreateReaderRequest request, String photo, List<Genre> interestList);
}
