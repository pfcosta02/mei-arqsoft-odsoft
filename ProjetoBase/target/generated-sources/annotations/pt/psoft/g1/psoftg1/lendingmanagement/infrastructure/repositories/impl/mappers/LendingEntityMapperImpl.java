package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingNumberEntity;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-30T16:59:33+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (Oracle Corporation)"
)
@Component
public class LendingEntityMapperImpl implements LendingEntityMapper {

    @Autowired
    private ReaderDetailsEntityMapper readerDetailsEntityMapper;
    @Autowired
    private BookEntityMapper bookEntityMapper;

    @Override
    public Lending toModel(LendingEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Lending.LendingBuilder lending = Lending.builder();

        lending.book( bookEntityMapper.toModel( entity.getBook() ) );
        lending.readerDetails( readerDetailsEntityMapper.toModel( entity.getReaderDetails() ) );
        lending.lendingNumber( map( entity.getLendingNumber() ) );
        lending.startDate( entity.getStartDate() );
        lending.limitDate( entity.getLimitDate() );
        lending.returnedDate( entity.getReturnedDate() );
        lending.fineValuePerDayInCents( entity.getFineValuePerDayInCents() );

        return lending.build();
    }

    @Override
    public LendingEntity toEntity(Lending model) {
        if ( model == null ) {
            return null;
        }

        BookEntity book = null;
        ReaderDetailsEntity readerDetails = null;
        LendingNumberEntity lendingNumber = null;
        LocalDate startDate = null;
        LocalDate limitDate = null;
        LocalDate returnedDate = null;
        int fineValuePerDayInCents = 0;
        String commentary = null;

        book = bookEntityMapper.toEntity( model.getBook() );
        readerDetails = readerDetailsEntityMapper.toEntity( model.getReaderDetails() );
        lendingNumber = map( model.getLendingNumber() );
        startDate = model.getStartDate();
        limitDate = model.getLimitDate();
        returnedDate = model.getReturnedDate();
        fineValuePerDayInCents = model.getFineValuePerDayInCents();
        commentary = model.getCommentary();

        LendingEntity lendingEntity = new LendingEntity( book, readerDetails, lendingNumber, startDate, limitDate, returnedDate, fineValuePerDayInCents, commentary );

        return lendingEntity;
    }
}
