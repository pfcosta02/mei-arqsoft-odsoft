package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.BirthDateEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.PhoneNumberEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderNumberEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserEntityMapper;

@Profile("jpa")
@Mapper(componentModel = "spring", uses = { UserEntityMapper.class})
public interface ReaderDetailsEntityMapper
{

    ReaderDetails toModel(ReaderDetailsEntity entity);

    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "toPhoneNumberEntity")
    @Mapping(target = "readerNumber", source = "readerNumber", qualifiedByName = "toReaderNumberEntity")
    @Mapping(target = "marketingConsent", source = "marketingConsent")
    @Mapping(target = "thirdPartySharingConsent", source = "thirdPartySharingConsent")
    @Mapping(target = "photo", source = "photo")
    ReaderDetailsEntity toEntity(ReaderDetails model);

    default String map(PhotoEntity value)
    {
        return value == null ? null : value.getPhotoFile();
    }

    default String map(NameEntity value)
    {
        return value == null ? null : value.getName();
    }

    @Named("toPhoneNumberEntity")
    default PhoneNumberEntity toPhoneNumberEntity(String value) {
        return value == null ? null : new PhoneNumberEntity(value);
    }

    @Named("toReaderNumberEntity")
    default ReaderNumberEntity toReaderNumberEntity(String value) {
        return value == null ? null : new ReaderNumberEntity(value);
    }

    default BirthDate map(BirthDateEntity value)
    {
        return value == null ? null : new BirthDate(value.getBirthDate().toString());
    }

    default BirthDate toBirthDate(String value)
    {
        return new BirthDate(value);
    }

    default ReaderNumber map(ReaderNumberEntity value)
    {
        return value == null ? null : new ReaderNumber(value.getReaderNumber());
    }

    default ReaderNumber map(int value)
    {
        return new ReaderNumber(value);
    }

    default PhoneNumber toPhoneNumber(String value)
    {
        return value == null ? null : new PhoneNumber(value);
    }
}