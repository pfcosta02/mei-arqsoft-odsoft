package pt.psoft.g1.psoftg1.readermanagement.services;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T11:51:43+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class ReaderMapperImpl extends ReaderMapper {

    @Autowired
    private ReaderDetailsEntityMapper readerDetailsEntityMapper;

    @Override
    public Reader createReader(CreateReaderRequest request) {
        if ( request == null ) {
            return null;
        }

        String username = null;
        String password = null;

        username = request.getUsername();
        password = request.getPassword();

        Reader reader = new Reader( username, password );

        reader.setName( request.getFullName() );

        return reader;
    }

    @Override
    public ReaderDetails createReaderDetails(int readerNumber, Reader reader, CreateReaderRequest request, String photoURI, List<Genre> interestList) {
        if ( reader == null && request == null && photoURI == null && interestList == null ) {
            return null;
        }

        ReaderDetails readerDetails = new ReaderDetails();

        if ( reader != null ) {
            readerDetails.setVersion( reader.getVersion() );
        }
        if ( request != null ) {
            readerDetails.setGdprConsent( request.getGdpr() );
            readerDetails.setMarketingConsent( request.getMarketing() );
            readerDetails.setThirdPartySharingConsent( request.getThirdParty() );
            readerDetails.setBirthDate( readerDetailsEntityMapper.toBirthDate( request.getBirthDate() ) );
            readerDetails.setPhoneNumber( readerDetailsEntityMapper.tPhoneNumber( request.getPhoneNumber() ) );
        }
        readerDetails.setReaderNumber( readerDetailsEntityMapper.map( readerNumber ) );
        readerDetails.setPhoto( photoURI );
        List<Genre> list = interestList;
        if ( list != null ) {
            readerDetails.setInterestList( new ArrayList<Genre>( list ) );
        }

        return readerDetails;
    }
}
