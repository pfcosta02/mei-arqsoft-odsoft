package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.DescriptionEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.IsbnEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.TitleEntity;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreEntityMapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoEntityMapper;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-23T12:28:42+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.9 (Microsoft)"
)
@Component
public class BookEntityMapperImpl implements BookEntityMapper {

    @Autowired
    private GenreEntityMapper genreEntityMapper;
    @Autowired
    private AuthorEntityMapper authorEntityMapper;
    @Autowired
    private PhotoEntityMapper photoEntityMapper;

    @Override
    public Book toModel(BookEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String photoURI = null;
        String title = null;
        String isbn = null;
        String description = null;
        Genre genre = null;
        List<Author> authors = null;

        photoURI = photoEntityMapper.map( entity.getPhoto() );
        title = map( entity.getTitle() );
        isbn = entity.getIsbn();
        description = entity.getDescription();
        genre = genreEntityMapper.toModel( entity.getGenre() );
        authors = authorEntityListToAuthorList( entity.getAuthors() );

        Book book = new Book( isbn, title, description, genre, authors, photoURI );

        book.setPhoto( photoEntityMapper.map( entity.getPhoto() ) );
        book.pk = entity.getPk();

        return book;
    }

    @Override
    public BookEntity toEntity(Book model) {
        if ( model == null ) {
            return null;
        }

        TitleEntity title = null;
        IsbnEntity isbn = null;
        DescriptionEntity description = null;
        GenreEntity genre = null;
        List<AuthorEntity> authors = null;

        title = titleToTitleEntity( model.getTitle() );
        isbn = isbnToIsbnEntity( model.getIsbn() );
        description = descriptionToDescriptionEntity( model.getDescription() );
        genre = genreEntityMapper.toEntity( model.getGenre() );
        authors = authorListToAuthorEntityList( model.getAuthors() );

        String photoURI = null;

        BookEntity bookEntity = new BookEntity( isbn, title, description, genre, authors, photoURI );

        bookEntity.setPhoto( photoEntityMapper.toEntity( model.getPhoto() ) );

        return bookEntity;
    }

    protected List<Author> authorEntityListToAuthorList(List<AuthorEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<Author> list1 = new ArrayList<Author>( list.size() );
        for ( AuthorEntity authorEntity : list ) {
            list1.add( authorEntityMapper.toModel( authorEntity ) );
        }

        return list1;
    }

    protected TitleEntity titleToTitleEntity(Title title) {
        if ( title == null ) {
            return null;
        }

        String title1 = null;

        title1 = title.getTitle();

        TitleEntity titleEntity = new TitleEntity( title1 );

        return titleEntity;
    }

    protected IsbnEntity isbnToIsbnEntity(Isbn isbn) {
        if ( isbn == null ) {
            return null;
        }

        String isbn1 = null;

        isbn1 = isbn.getIsbn();

        IsbnEntity isbnEntity = new IsbnEntity( isbn1 );

        return isbnEntity;
    }

    protected DescriptionEntity descriptionToDescriptionEntity(Description description) {
        if ( description == null ) {
            return null;
        }

        String description1 = null;

        description1 = description.getDescription();

        DescriptionEntity descriptionEntity = new DescriptionEntity( description1 );

        return descriptionEntity;
    }

    protected List<AuthorEntity> authorListToAuthorEntityList(List<Author> list) {
        if ( list == null ) {
            return null;
        }

        List<AuthorEntity> list1 = new ArrayList<AuthorEntity>( list.size() );
        for ( Author author : list ) {
            list1.add( authorEntityMapper.toEntity( author ) );
        }

        return list1;
    }
}
