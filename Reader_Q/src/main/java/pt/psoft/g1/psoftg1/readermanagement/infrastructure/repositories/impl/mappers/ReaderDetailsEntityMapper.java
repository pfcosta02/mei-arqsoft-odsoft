package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers;

import org.mapstruct.Mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.*;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

@Mapper(componentModel = "spring")
public interface ReaderDetailsEntityMapper
{
    ReaderDetails toModel(ReaderDetailsEntity entity);

    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "toPhoneNumberEntity")
    @Mapping(target = "readerNumber", source = "readerNumber", qualifiedByName = "toReaderNumberEntity")
    @Mapping(target = "marketingConsent", source = "marketingConsent")
    @Mapping(target = "thirdPartySharingConsent", source = "thirdPartySharingConsent")
    @Mapping(target = "photo", source = "photo")
    @Mapping(target = "version", source = "version")
    ReaderDetailsEntity toEntity(ReaderDetails model);

    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "toPhoneNumberEntity")
    ReaderDetailsTempEntity toTempEntity(ReaderDetails model);

    @Mapping(target = "readerNumber", ignore = true)
    ReaderDetails toModelFromTemp(ReaderDetailsTempEntity tempEntity);

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

    @Named("toBirthDate")
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

    @Named("toPhoneNumber")
    default PhoneNumber tPhoneNumber(String value)
    {
        return value == null ? null : new PhoneNumber(value);
    }

    default String map(Name value)
    {
        return value == null ? null : value.getName();
    }
}
