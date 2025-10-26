package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.BirthDateMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.PhoneNumberMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderNumberMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.NameMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserMapperMongoDB;

@Profile("mongodb")
@Mapper(componentModel = "spring", uses = { UserMapperMongoDB.class})
public interface ReaderDetailsMapperMongoDB
{
    ReaderDetails toModel(ReaderDetailsMongoDB entity);

    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "toPhoneNumberMongoDB")
    @Mapping(target = "readerNumber", source = "readerNumber", qualifiedByName = "toReaderNumberMongoDB")
    @Mapping(target = "marketingConsent", source = "marketingConsent")
    @Mapping(target = "thirdPartySharingConsent", source = "thirdPartySharingConsent")
    @Mapping(target = "photo", source = "photo")
    ReaderDetailsMongoDB toEntity(ReaderDetails model);

    default String map(PhotoMongoDB value)
    {
        return value == null ? null : value.getPhotoFile();
    }

    default String map(NameMongoDB value)
    {
        return value == null ? null : value.getName();
    }

    @Named("toPhoneNumberMongoDB")
    default PhoneNumberMongoDB toPhoneNumberMongoDB(String value) {
        return value == null ? null : new PhoneNumberMongoDB(value);
    }

    @Named("toReaderNumberMongoDB")
    default ReaderNumberMongoDB toReaderNumberMongoDB(String value) {
        return value == null ? null : new ReaderNumberMongoDB(value);
    }

    default BirthDate map(BirthDateMongoDB value)
    {
        return value == null ? null : new BirthDate(value.getBirthDate().toString());
    }

    default BirthDate toBirthDate(String value)
    {
        return new BirthDate(value);
    }

    default ReaderNumber map(ReaderNumberMongoDB value)
    {
        return value == null ? null : new ReaderNumber(value.getReaderNumber());
    }

    default ReaderNumber map(int value)
    {
        return new ReaderNumber(value);
    }

    default PhoneNumber tPhoneNumber(String value)
    {
        return value == null ? null : new PhoneNumber(value);
    }
}