package pt.psoft.g1.psoftg1.bookmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;
import pt.psoft.g1.psoftg1.bookmanagement.model.*;
import pt.psoft.g1.psoftg1.bookmanagement.model.DTOs.BookTempCreatedDTO;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookTempEntity;
import pt.psoft.g1.psoftg1.bookmanagement.publishers.BookEventsPublisher;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookTempRepository;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.isbn.services.IsbnProviderFactory;
import pt.psoft.g1.psoftg1.shared.model.BookEvents;
import pt.psoft.g1.psoftg1.shared.model.OutboxEnum;
import pt.psoft.g1.psoftg1.shared.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.shared.repositories.OutboxEventRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@PropertySource({"classpath:config/library.properties"})
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final GenreRepository genreRepository;
	private final AuthorRepository authorRepository;
	private final PhotoRepository photoRepository;
    private final IsbnProviderFactory isbnProviderFactory;

	private final BookEventsPublisher bookEventsPublisher;
	private final BookTempRepository bookTempRepository;
	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	@Value("${suggestionsLimitPerGenre}")
	private long suggestionsLimitPerGenre;

	@Override
	public Book create(CreateBookRequest request, String isbn) {

		final String title = request.getTitle();
		final String description = request.getDescription();
		final String photoURI = request.getPhotoURI();
		final String genre = request.getGenre();
		final List<Long> authorIds = request.getAuthors();

		Book savedBook = create(isbn, title, description, photoURI, genre, authorIds);

		if( savedBook!=null ) {
			bookEventsPublisher.sendBookCreated(savedBook);
		}

		return savedBook;
	}


	// NOVO METODO PARA SAGA
	@Transactional
	@Override
	public Book create(CreateBookAuthorGenreRequest request, String isbn) {

		final String title = request.getTitle();
		final String description = request.getDescription();
		final String photoURI = request.getPhotoURI();
		final String genre = request.getGenre();
		final CreateAuthorRequest author = request.getAuthor();

		BookTempEntity temp = new BookTempEntity(
				isbn,
				title,
				description,
				author.getName(),
				author.getBio(),
				request.getGenre()
		);

		bookTempRepository.save(temp);

		try {
			BookTempCreatedDTO bookTempCreatedDTO = new BookTempCreatedDTO(isbn, author.getName(), author.getBio());
			String payload = objectMapper.writeValueAsString(bookTempCreatedDTO);

			OutboxEvent event = new OutboxEvent();
			event.setAggregateId(isbn);
			event.setEventType(BookEvents.TEMP_BOOK_CREATED);
			event.setPayload(payload);
			event.setStatus(OutboxEnum.NEW);

			outboxEventRepository.save(event);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao salvar evento Outbox", e);
		}

		return null;
	}

	@Override
	public Book create(BookViewAMQP bookViewAMQP) {

		final String isbn = bookViewAMQP.getIsbn();
		final String description = bookViewAMQP.getDescription();
		final String title = bookViewAMQP.getTitle();
		final String photoURI = null;
		final String genre = bookViewAMQP.getGenre();
		final List<Long> authorIds = bookViewAMQP.getAuthorIds();

		Book bookCreated = create(isbn, title, description, photoURI, genre, authorIds);

		return bookCreated;
	}

	private Book create( String isbn,
						 String title,
						 String description,
						 String photoURI,
						 String genreName,
						 List<Long> authorIds) {

		if (bookRepository.findByIsbn(isbn).isPresent()) {
			throw new ConflictException("Book with ISBN " + isbn + " already exists");
		}

		List<Author> authors = getAuthors(authorIds);

		final Genre genre = genreRepository.findByString(String.valueOf(genreName))
				.orElseThrow(() -> new NotFoundException("Genre not found"));

		Book newBook = new Book(isbn, title, description, genre, authors, photoURI);

		Book savedBook = bookRepository.save(newBook);

		return savedBook;
	}

	@Override
	public Book update(UpdateBookRequest request, String currentVersion) {

        var book = findByIsbn(request.getIsbn());
        if(request.getAuthors()!= null) {
            List<Long> authorNumbers = request.getAuthors();
            List<Author> authors = new ArrayList<>();
            for (Long authorNumber : authorNumbers) {
                Optional<Author> temp = authorRepository.findByAuthorNumber(authorNumber);
                if (temp.isEmpty()) {
                    continue;
                }
                Author author = temp.get();
                authors.add(author);
            }

            request.setAuthorObjList(authors);
        }

		MultipartFile photo = request.getPhoto();
		String photoURI = request.getPhotoURI();
		if(photo == null && photoURI != null || photo != null && photoURI == null) {
			request.setPhoto(null);
			request.setPhotoURI(null);
		}

        if (request.getGenre() != null) {
            Optional<Genre> genre = genreRepository.findByString(request.getGenre());
            if (genre.isEmpty()) {
                throw new NotFoundException("Genre not found");
            }
            request.setGenreObj(genre.get());
        }

        book.applyPatch(Long.parseLong(currentVersion), request);

		Book updatedBook = bookRepository.save(book);

//		if( updatedBook!=null ) {
//			bookEventsPublisher.sendBookUpdated(updatedBook, updatedBook.getVersion());
//		}

		return book;
	}

	@Override
	public Book save(Book book) {
		return this.bookRepository.save(book);
	}

	@Override
	public Book removeBookPhoto(String isbn, long desiredVersion) {
		Book book = this.findByIsbn(isbn);
		String photoFile;
		try {
			photoFile = book.getPhoto().getPhotoFile();
		}catch (NullPointerException e){
			throw new NotFoundException("Book did not have a photo assigned to it.");
		}

		book.removePhoto(desiredVersion);
		var updatedBook = bookRepository.save(book);
		photoRepository.deleteByPhotoFile(photoFile);
		return updatedBook;
	}

	public Book findByIsbn(String isbn) {
		return this.bookRepository.findByIsbn(isbn)
				.orElseThrow(() -> new NotFoundException(Book.class, isbn));
	}

	private List<Author> getAuthors(List<Long> authorNumbers) {

		List<Author> authors = new ArrayList<>();
		for (Long authorNumber : authorNumbers) {

			Optional<Author> temp = authorRepository.findByAuthorNumber(authorNumber);
			if (temp.isEmpty()) {
				continue;
			}

			Author author = temp.get();
			authors.add(author);
		}

		return authors;
	}
}
