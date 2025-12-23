package pt.psoft.g1.psoftg1.lendingmanagement.api;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T11:51:43+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class LendingViewMapperImpl extends LendingViewMapper {

    @Override
    public LendingView toLendingView(Lending lending) {
        if ( lending == null ) {
            return null;
        }

        LendingView lendingView = new LendingView();

        lendingView.set_links( lendingToLendingLinksView( lending ) );
        lendingView.setLendingNumber( map( lending.getLendingNumber() ) );
        lendingView.setBookTitle( map( lendingBookTitle( lending ) ) );
        lendingView.setReturnedDate( lending.getReturnedDate() );
        lendingView.setStartDate( lending.getStartDate() );
        lendingView.setLimitDate( lending.getLimitDate() );
        lendingView.setDaysUntilReturn( mapOpt( lending.getDaysUntilReturn() ) );
        lendingView.setDaysOverdue( mapOpt( lending.getDaysOverdue() ) );

        lendingView.setFineValueInCents( lending.getFineValueInCents().orElse(null) );

        return lendingView;
    }

    @Override
    public List<LendingView> toLendingView(List<Lending> lendings) {
        if ( lendings == null ) {
            return null;
        }

        List<LendingView> list = new ArrayList<LendingView>( lendings.size() );
        for ( Lending lending : lendings ) {
            list.add( toLendingView( lending ) );
        }

        return list;
    }

    @Override
    public LendingsAverageDurationView toLendingsAverageDurationView(Double lendingsAverageDuration) {
        if ( lendingsAverageDuration == null ) {
            return null;
        }

        LendingsAverageDurationView lendingsAverageDurationView = new LendingsAverageDurationView();

        lendingsAverageDurationView.setLendingsAverageDuration( lendingsAverageDuration );

        return lendingsAverageDurationView;
    }

    protected LendingLinksView lendingToLendingLinksView(Lending lending) {
        if ( lending == null ) {
            return null;
        }

        LendingLinksView lendingLinksView = new LendingLinksView();

        lendingLinksView.setSelf( mapLendingLink( lending ) );
        lendingLinksView.setBook( mapBookLink( lending.getBook() ) );
        lendingLinksView.setReader( mapReaderLink( lending.getReaderDetails() ) );

        return lendingLinksView;
    }

    private Title lendingBookTitle(Lending lending) {
        if ( lending == null ) {
            return null;
        }
        Book book = lending.getBook();
        if ( book == null ) {
            return null;
        }
        Title title = book.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}
