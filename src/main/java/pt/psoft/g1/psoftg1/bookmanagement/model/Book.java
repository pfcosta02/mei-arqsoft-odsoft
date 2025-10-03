package pt.psoft.g1.psoftg1.bookmanagement.model;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.services.UpdateBookRequest;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import java.util.List;
import java.util.Objects;

import org.hibernate.StaleObjectStateException;

public class Book extends EntityWithPhoto
{
    private Long version;
    private Isbn isbn;
    private Title title;
    private Description description;
    private Genre genre;
    private List<Author> authors;

    protected Book() { }

    public Book(Isbn isbn, Title title, Description description, Genre genre, List<Author> authors, String photoURI)
    {
        setTitle(title);
        setIsbn(isbn);
        setGenre(genre);
        setAuthors(authors);
        setPhotoInternal(photoURI);

        this.version = 0L;
    }

    public Book(String isbn, String title, String description, Genre genre, List<Author> authors, String photoURI)
    {
        // Avoid duplicated code
        this(new Isbn(isbn), new Title(title), new Description(description), genre, authors, photoURI);
    }

    // Getters
    public Isbn getIsbn() { return isbn; }
    public Title getTitle() { return title; }
    public Description getDescription() { return description; }
    public Genre getGenre() { return genre; }
    public List<Author> getAuthors() { return authors; }
    public Long getVersion() { return version; }

    // Setters
    private void setTitle(Title title) {this.title = title;}
    private void setIsbn(Isbn isbn) { this.isbn = isbn;}
    private void setDescription(Description description) {this.description = description; }
    private void setGenre(Genre genre)
    {
        if(genre == null)
        {
            throw new IllegalArgumentException("Genre cannot be null");
        }

        this.genre = genre;
    }
    private void setAuthors(List<Author> authors)
    {
        if(authors == null || authors.isEmpty())
        {
            throw new IllegalArgumentException("Authors cannot be empty");
        }

        this.authors = authors;
    }

    // regras de negócio
    public void applyPatch(final Long expectedVersion, UpdateBookRequest request)
    {
        String title = request.getTitle();
        String description = request.getDescription();
        Genre genre = request.getGenreObj();
        List<Author> authors = request.getAuthorObjList();
        String photoURI = request.getPhotoURI();
        if(title != null)
        {
            setTitle(new Title(title));
        }

        if(description != null)
        {
            setDescription(new Description(description));
        }

        if(genre != null)
        {
            setGenre(genre);
        }

        if(authors != null)
        {
            setAuthors(authors);
        }

        if(photoURI != null)
        {
            setPhotoInternal(photoURI);
        }
    }

    public void removePhoto(Long expectedVersion)
    {
        if(!Objects.equals(this.version, expectedVersion))
        {
            throw new ConflictException("Provided version does not match latest version of this object");
        }

        setPhotoInternal((String)null);
    }
}
