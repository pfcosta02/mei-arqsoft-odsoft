package pt.psoft.g1.psoftg1.bookmanagement.api;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-20T16:56:20+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class BookViewMapperImpl extends BookViewMapper {

    @Override
    public BookView toBookView(Book book) {
        if ( book == null ) {
            return null;
        }

        BookView bookView = new BookView();

        bookView.setGenre( map( book.getGenre() ) );
        bookView.setIsbn( map( book.getIsbn() ) );
        bookView.setDescription( map( book.getDescription() ) );
        bookView.setTitle( map( book.getTitle() ) );

        bookView.setAuthors( mapAuthors(book.getAuthors()) );
        bookView.set_links( mapLinks(book) );

        return bookView;
    }

    @Override
    public List<BookView> toBookView(List<Book> bookList) {
        if ( bookList == null ) {
            return null;
        }

        List<BookView> list = new ArrayList<BookView>( bookList.size() );
        for ( Book book : bookList ) {
            list.add( toBookView( book ) );
        }

        return list;
    }

    @Override
    public BookCountView toBookCountView(BookCountDTO bookCountDto) {
        if ( bookCountDto == null ) {
            return null;
        }

        BookCountView bookCountView = new BookCountView();

        bookCountView.setBookView( bookEntityToBookView( bookCountDto.getBook() ) );
        bookCountView.setLendingCount( bookCountDto.getLendingCount() );

        return bookCountView;
    }

    @Override
    public List<BookCountView> toBookCountView(List<BookCountDTO> bookCountDtos) {
        if ( bookCountDtos == null ) {
            return null;
        }

        List<BookCountView> list = new ArrayList<BookCountView>( bookCountDtos.size() );
        for ( BookCountDTO bookCountDTO : bookCountDtos ) {
            list.add( toBookCountView( bookCountDTO ) );
        }

        return list;
    }

    @Override
    public BookAverageLendingDurationView toBookAverageLendingDurationView(Book book, Double averageLendingDuration) {
        if ( book == null && averageLendingDuration == null ) {
            return null;
        }

        BookAverageLendingDurationView bookAverageLendingDurationView = new BookAverageLendingDurationView();

        bookAverageLendingDurationView.setBook( toBookView( book ) );
        bookAverageLendingDurationView.setAverageLendingDuration( averageLendingDuration );

        return bookAverageLendingDurationView;
    }

    protected List<String> authorEntityListToStringList(List<AuthorEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( AuthorEntity authorEntity : list ) {
            list1.add( map( authorEntity ) );
        }

        return list1;
    }

    protected BookView bookEntityToBookView(BookEntity bookEntity) {
        if ( bookEntity == null ) {
            return null;
        }

        BookView bookView = new BookView();

        bookView.setTitle( map( bookEntity.getTitle() ) );
        bookView.setAuthors( authorEntityListToStringList( bookEntity.getAuthors() ) );
        bookView.setGenre( map( bookEntity.getGenre() ) );
        bookView.setDescription( map( bookEntity.getDescription() ) );
        bookView.setIsbn( map( bookEntity.getIsbn() ) );

        return bookView;
    }
}
