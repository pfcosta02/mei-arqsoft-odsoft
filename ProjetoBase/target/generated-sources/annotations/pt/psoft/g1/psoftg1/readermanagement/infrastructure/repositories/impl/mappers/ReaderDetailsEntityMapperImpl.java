package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.BirthDateEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.PhoneNumberEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderNumberEntity;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-30T16:59:33+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class ReaderDetailsEntityMapperImpl implements ReaderDetailsEntityMapper {

    @Autowired
    private UserEntityMapper userEntityMapper;

    @Override
    public ReaderDetails toModel(ReaderDetailsEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ReaderDetails readerDetails = new ReaderDetails();

        readerDetails.setPhoto( map( entity.getPhoto() ) );
        readerDetails.setReader( userEntityMapper.toModel( entity.getReader() ) );
        readerDetails.setReaderNumber( map( entity.getReaderNumber() ) );
        readerDetails.setVersion( entity.getVersion() );
        readerDetails.setBirthDate( map( entity.getBirthDate() ) );
        readerDetails.setPhoneNumber( phoneNumberEntityToPhoneNumber( entity.getPhoneNumber() ) );
        readerDetails.setGdprConsent( entity.isGdprConsent() );
        readerDetails.setMarketingConsent( entity.isMarketingConsent() );
        readerDetails.setThirdPartySharingConsent( entity.isThirdPartySharingConsent() );
        readerDetails.setInterestList( genreEntityListToGenreList( entity.getInterestList() ) );
        readerDetails.pk = entity.getPk();

        return readerDetails;
    }

    @Override
    public ReaderDetailsEntity toEntity(ReaderDetails model) {
        if ( model == null ) {
            return null;
        }

        PhoneNumberEntity phoneNumber = null;
        ReaderNumberEntity readerNumber = null;
        ReaderEntity reader = null;
        BirthDateEntity birthDate = null;
        List<GenreEntity> interestList = null;

        phoneNumber = toPhoneNumberEntity( model.getPhoneNumber() );
        readerNumber = toReaderNumberEntity( model.getReaderNumber() );
        reader = userEntityMapper.toEntity( model.getReader() );
        birthDate = birthDateToBirthDateEntity( model.getBirthDate() );
        interestList = genreListToGenreEntityList( model.getInterestList() );

        boolean gdpr = false;
        boolean marketing = false;
        boolean thirdParty = false;
        PhotoEntity photoURI = null;

        ReaderDetailsEntity readerDetailsEntity = new ReaderDetailsEntity( readerNumber, reader, birthDate, phoneNumber, gdpr, marketing, thirdParty, photoURI, interestList );

        readerDetailsEntity.setMarketingConsent( model.isMarketingConsent() );
        readerDetailsEntity.setThirdPartySharingConsent( model.isThirdPartySharingConsent() );
        readerDetailsEntity.setPhoto( photoToPhotoEntity( model.getPhoto() ) );
        readerDetailsEntity.setGdprConsent( model.isGdprConsent() );
        readerDetailsEntity.setVersion( model.getVersion() );

        return readerDetailsEntity;
    }

    protected PhoneNumber phoneNumberEntityToPhoneNumber(PhoneNumberEntity phoneNumberEntity) {
        if ( phoneNumberEntity == null ) {
            return null;
        }

        String phoneNumber = null;

        phoneNumber = phoneNumberEntity.getPhoneNumber();

        PhoneNumber phoneNumber1 = new PhoneNumber( phoneNumber );

        return phoneNumber1;
    }

    protected Genre genreEntityToGenre(GenreEntity genreEntity) {
        if ( genreEntity == null ) {
            return null;
        }

        String genre = null;

        genre = genreEntity.getGenre();

        Genre genre1 = new Genre( genre );

        genre1.pk = genreEntity.getPk();

        return genre1;
    }

    protected List<Genre> genreEntityListToGenreList(List<GenreEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Genre> list1 = new ArrayList<Genre>( list.size() );
        for ( GenreEntity genreEntity : list ) {
            list1.add( genreEntityToGenre( genreEntity ) );
        }

        return list1;
    }

    protected PhotoEntity photoToPhotoEntity(Photo photo) {
        if ( photo == null ) {
            return null;
        }

        String photoFile = null;

        photoFile = photo.getPhotoFile();

        PhotoEntity photoEntity = new PhotoEntity( photoFile );

        return photoEntity;
    }

    protected BirthDateEntity birthDateToBirthDateEntity(BirthDate birthDate) {
        if ( birthDate == null ) {
            return null;
        }

        LocalDate birthDate1 = null;

        birthDate1 = birthDate.getBirthDate();

        BirthDateEntity birthDateEntity = new BirthDateEntity( birthDate1 );

        return birthDateEntity;
    }

    protected GenreEntity genreToGenreEntity(Genre genre) {
        if ( genre == null ) {
            return null;
        }

        String genre1 = null;

        genre1 = genre.getGenre();

        GenreEntity genreEntity = new GenreEntity( genre1 );

        return genreEntity;
    }

    protected List<GenreEntity> genreListToGenreEntityList(List<Genre> list) {
        if ( list == null ) {
            return null;
        }

        List<GenreEntity> list1 = new ArrayList<GenreEntity>( list.size() );
        for ( Genre genre : list ) {
            list1.add( genreToGenreEntity( genre ) );
        }

        return list1;
    }
}
