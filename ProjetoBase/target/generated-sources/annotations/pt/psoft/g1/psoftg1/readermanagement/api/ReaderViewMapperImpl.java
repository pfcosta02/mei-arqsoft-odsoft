package pt.psoft.g1.psoftg1.readermanagement.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T11:51:44+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class ReaderViewMapperImpl extends ReaderViewMapper {

    @Override
    public ReaderView toReaderView(ReaderDetails readerDetails) {
        if ( readerDetails == null ) {
            return null;
        }

        ReaderView readerView = new ReaderView();

        readerView.setFullName( map( readerDetailsReaderNameName( readerDetails ) ) );
        readerView.setEmail( map( readerDetailsReaderUsername( readerDetails ) ) );
        readerView.setBirthDate( map( readerDetailsBirthDateBirthDate( readerDetails ) ) );
        readerView.setPhoneNumber( map( readerDetails.getPhoneNumber() ) );
        readerView.setGdprConsent( readerDetails.isGdprConsent() );
        readerView.setReaderNumber( map( readerDetails.getReaderNumber() ) );
        readerView.setMarketingConsent( readerDetails.isMarketingConsent() );
        readerView.setThirdPartySharingConsent( readerDetails.isThirdPartySharingConsent() );

        readerView.setPhoto( generatePhotoUrl(readerDetails) );
        readerView.setInterestList( mapInterestList(readerDetails.getInterestList()) );

        return readerView;
    }

    @Override
    public ReaderQuoteView toReaderQuoteView(ReaderDetails readerDetails) {
        if ( readerDetails == null ) {
            return null;
        }

        ReaderQuoteView readerQuoteView = new ReaderQuoteView();

        readerQuoteView.setFullName( map( readerDetailsReaderNameName( readerDetails ) ) );
        readerQuoteView.setEmail( map( readerDetailsReaderUsername( readerDetails ) ) );
        readerQuoteView.setBirthDate( map( readerDetailsBirthDateBirthDate( readerDetails ) ) );
        readerQuoteView.setPhoneNumber( map( readerDetails.getPhoneNumber() ) );
        readerQuoteView.setGdprConsent( readerDetails.isGdprConsent() );
        readerQuoteView.setReaderNumber( map( readerDetails.getReaderNumber() ) );
        readerQuoteView.setMarketingConsent( readerDetails.isMarketingConsent() );
        readerQuoteView.setThirdPartySharingConsent( readerDetails.isThirdPartySharingConsent() );

        readerQuoteView.setPhoto( generatePhotoUrl(readerDetails) );
        readerQuoteView.setInterestList( mapInterestList(readerDetails.getInterestList()) );

        return readerQuoteView;
    }

    @Override
    public List<ReaderView> toReaderView(Iterable<ReaderDetails> readerList) {
        if ( readerList == null ) {
            return null;
        }

        List<ReaderView> list = new ArrayList<ReaderView>();
        for ( ReaderDetails readerDetails : readerList ) {
            list.add( readerDetailsToReaderView( readerDetails ) );
        }

        return list;
    }

    @Override
    public ReaderCountView toReaderCountView(ReaderBookCountDTO readerBookCountDTO) {
        if ( readerBookCountDTO == null ) {
            return null;
        }

        ReaderCountView readerCountView = new ReaderCountView();

        readerCountView.setReaderView( readerDetailsEntityToReaderView( readerBookCountDTO.getReaderDetails() ) );
        readerCountView.setLendingCount( readerBookCountDTO.getLendingCount() );

        return readerCountView;
    }

    @Override
    public List<ReaderCountView> toReaderCountViewList(List<ReaderBookCountDTO> readerBookCountDTOList) {
        if ( readerBookCountDTOList == null ) {
            return null;
        }

        List<ReaderCountView> list = new ArrayList<ReaderCountView>( readerBookCountDTOList.size() );
        for ( ReaderBookCountDTO readerBookCountDTO : readerBookCountDTOList ) {
            list.add( toReaderCountView( readerBookCountDTO ) );
        }

        return list;
    }

    private String readerDetailsReaderNameName(ReaderDetails readerDetails) {
        if ( readerDetails == null ) {
            return null;
        }
        Reader reader = readerDetails.getReader();
        if ( reader == null ) {
            return null;
        }
        Name name = reader.getName();
        if ( name == null ) {
            return null;
        }
        String name1 = name.getName();
        if ( name1 == null ) {
            return null;
        }
        return name1;
    }

    private String readerDetailsReaderUsername(ReaderDetails readerDetails) {
        if ( readerDetails == null ) {
            return null;
        }
        Reader reader = readerDetails.getReader();
        if ( reader == null ) {
            return null;
        }
        String username = reader.getUsername();
        if ( username == null ) {
            return null;
        }
        return username;
    }

    private LocalDate readerDetailsBirthDateBirthDate(ReaderDetails readerDetails) {
        if ( readerDetails == null ) {
            return null;
        }
        BirthDate birthDate = readerDetails.getBirthDate();
        if ( birthDate == null ) {
            return null;
        }
        LocalDate birthDate1 = birthDate.getBirthDate();
        if ( birthDate1 == null ) {
            return null;
        }
        return birthDate1;
    }

    protected ReaderView readerDetailsToReaderView(ReaderDetails readerDetails) {
        if ( readerDetails == null ) {
            return null;
        }

        ReaderView readerView = new ReaderView();

        readerView.setReaderNumber( map( readerDetails.getReaderNumber() ) );
        readerView.setBirthDate( map( readerDetails.getBirthDate() ) );
        readerView.setPhoneNumber( map( readerDetails.getPhoneNumber() ) );
        readerView.setPhoto( map( readerDetails.getPhoto() ) );
        readerView.setGdprConsent( readerDetails.isGdprConsent() );
        readerView.setMarketingConsent( readerDetails.isMarketingConsent() );
        readerView.setThirdPartySharingConsent( readerDetails.isThirdPartySharingConsent() );
        readerView.setInterestList( mapInterestList( readerDetails.getInterestList() ) );

        return readerView;
    }

    protected List<String> genreEntityListToStringList(List<GenreEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( GenreEntity genreEntity : list ) {
            list1.add( map( genreEntity ) );
        }

        return list1;
    }

    protected ReaderView readerDetailsEntityToReaderView(ReaderDetailsEntity readerDetailsEntity) {
        if ( readerDetailsEntity == null ) {
            return null;
        }

        ReaderView readerView = new ReaderView();

        readerView.setReaderNumber( map( readerDetailsEntity.getReaderNumber() ) );
        readerView.setBirthDate( map( readerDetailsEntity.getBirthDate() ) );
        readerView.setPhoneNumber( map( readerDetailsEntity.getPhoneNumber() ) );
        readerView.setPhoto( map( readerDetailsEntity.getPhoto() ) );
        readerView.setGdprConsent( readerDetailsEntity.isGdprConsent() );
        readerView.setMarketingConsent( readerDetailsEntity.isMarketingConsent() );
        readerView.setThirdPartySharingConsent( readerDetailsEntity.isThirdPartySharingConsent() );
        readerView.setInterestList( genreEntityListToStringList( readerDetailsEntity.getInterestList() ) );

        return readerView;
    }
}
