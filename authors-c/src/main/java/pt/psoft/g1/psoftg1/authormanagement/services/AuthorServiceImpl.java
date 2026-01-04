package pt.psoft.g1.psoftg1.authormanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQPMapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.DTOs.AuthorTempCreatedDTO;
import pt.psoft.g1.psoftg1.authormanagement.model.DTOs.BookFinalizedDTO;
import pt.psoft.g1.psoftg1.authormanagement.model.DTOs.BookTempCreatedAuthorsDTO;
import pt.psoft.g1.psoftg1.authormanagement.model.DTOs.BookTempCreatedDTO;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorTempEntity;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.BioEntity;
import pt.psoft.g1.psoftg1.authormanagement.publishers.AuthorEventsPublisher;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorTempRepository;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
import pt.psoft.g1.psoftg1.shared.model.AuthorEvents;
import pt.psoft.g1.psoftg1.shared.model.BookEvents;
import pt.psoft.g1.psoftg1.shared.model.OutboxEnum;
import pt.psoft.g1.psoftg1.shared.model.relational.NameEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.shared.repositories.OutboxEventRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorMapper mapper;
    private final PhotoRepository photoRepository;
    private final IdGenerator idGenerator;
    private final AuthorTempRepository authorTempRepository;

    private final AuthorEventsPublisher authorEventsPublisher;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final AuthorViewAMQPMapper authorViewAMQPMapper;

    @Override
    public Iterable<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findByAuthorNumber(final String authorNumber) {
        return authorRepository.findByAuthorNumber(authorNumber);
    }

    @Override
    public List<Author> findByName(String name) {
        return authorRepository.searchByNameNameStartsWith(name);
    }

    @Override
    public Author create(final CreateAuthorRequest resource) {
        /*
         * Since photos can be null (no photo uploaded) that means the URI can be null as well.
         * To avoid the client sending false data, photoURI has to be set to any value / null
         * according to the MultipartFile photo object
         *
         * That means:
         * - photo = null && photoURI = null -> photo is removed
         * - photo = null && photoURI = validString -> ignored
         * - photo = validFile && photoURI = null -> ignored
         * - photo = validFile && photoURI = validString -> photo is set
         * */

        MultipartFile photo = resource.getPhoto();
        String photoURI = resource.getPhotoURI();
        if(photo == null && photoURI != null || photo != null && photoURI == null) {
            resource.setPhoto(null);
            resource.setPhotoURI(null);
        }
        final Author author = mapper.create(resource);
        author.setAuthorNumber(idGenerator.generateId());
        Author savedAuthor = authorRepository.save(author);

        if( savedAuthor!=null ) {
            authorEventsPublisher.sendAuthorCreated(savedAuthor);
        }

        return savedAuthor;
    }

    @Override
    public Author create(AuthorViewAMQP authorViewAMQP) {

        final String name = authorViewAMQP.getName();
        final String bio = authorViewAMQP.getBio();
        final String photoURI = null;
        final String authorNumber = authorViewAMQP.getAuthorNumber();
        final Author author = new Author(name, bio, photoURI);
        author.setAuthorNumber(authorNumber);
        return authorRepository.save(author);
    }

    @Override
    public void createTemp(BookTempCreatedDTO bookTempCreatedDTO) {

        final List<BookTempCreatedAuthorsDTO> authorsDTOs = bookTempCreatedDTO.getAuthorsDTOs();
        final String isbn = bookTempCreatedDTO.getIsbn();
        List<String> generatedAuthorNumbers = new ArrayList<>();

        for (BookTempCreatedAuthorsDTO authorDTO : authorsDTOs) {

            String authorNumber = idGenerator.generateId();

            AuthorTempEntity authorTemp = new AuthorTempEntity(
                    new NameEntity(authorDTO.getName()),
                    new BioEntity(authorDTO.getBio()),
                    isbn
            );

            authorTemp.setAuthorNumber(authorNumber);
            authorTempRepository.save(authorTemp);

            generatedAuthorNumbers.add(authorNumber);
        }

        try {
            AuthorTempCreatedDTO authorTempCreatedDTO = new AuthorTempCreatedDTO(isbn, generatedAuthorNumbers);
            String payload = objectMapper.writeValueAsString(authorTempCreatedDTO);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(isbn);
            event.setEventType(AuthorEvents.TEMP_AUTHOR_CREATED);
            event.setPayload(payload);
            event.setStatus(OutboxEnum.NEW);

            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar evento Outbox", e);
        }
    }

    @Transactional
    @Override
    public void updateTemp(BookFinalizedDTO bookFinalizedDTO) {
        // Encontrar AuthorTemp pelo AuthorNumber
        for(String authorNumber : bookFinalizedDTO.getAuthorNumbers()) {
            AuthorTempEntity authorTemp = authorTempRepository
                    .findByAuthorNumber(authorNumber)
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "AuthorTemp not found for authorNumber " + authorNumber
                            )
                    );

            Author author = fromAuthorTempEntitytoAuthor(authorTemp);
            author.setAuthorNumber(authorNumber);
            Author savedAuthor = authorRepository.save(author);

            AuthorViewAMQP authorViewAMQP = authorViewAMQPMapper.toAuthorViewAMQP(savedAuthor);
            authorViewAMQP.setVersion(savedAuthor.getVersion());

            try {
                String payload = objectMapper.writeValueAsString(authorViewAMQP);

                OutboxEvent event = new OutboxEvent();
                event.setAggregateId(author.getAuthorNumber());
                event.setEventType(AuthorEvents.AUTHOR_CREATED);
                event.setPayload(payload);
                event.setStatus(OutboxEnum.NEW);

                outboxEventRepository.save(event);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao salvar evento Outbox", e);
            }

            // Importante: remover o temporário
            authorTempRepository.delete(authorTemp);
        }
    }

    @Override
    public Author partialUpdate(final String authorNumber, final UpdateAuthorRequest request, final long desiredVersion) {
        // first let's check if the object exists so we don't create a new object with
        // save
        final var author = findByAuthorNumber(authorNumber)
                .orElseThrow(() -> new NotFoundException("Cannot update an object that does not yet exist"));
        /*
         * Since photos can be null (no photo uploaded) that means the URI can be null as well.
         * To avoid the client sending false data, photoURI has to be set to any value / null
         * according to the MultipartFile photo object
         *
         * That means:
         * - photo = null && photoURI = null -> photo is removed
         * - photo = null && photoURI = validString -> ignored
         * - photo = validFile && photoURI = null -> ignored
         * - photo = validFile && photoURI = validString -> photo is set
         * */

        MultipartFile photo = request.getPhoto();
        String photoURI = request.getPhotoURI();
        if(photo == null && photoURI != null || photo != null && photoURI == null) {
            request.setPhoto(null);
            request.setPhotoURI(null);
        }
        // since we got the object from the database we can check the version in memory
        // and apply the patch
        author.applyPatch(desiredVersion, request);

        // in the meantime some other user might have changed this object on the
        // database, so concurrency control will still be applied when we try to save
        // this updated object
        author.setAuthorNumber(idGenerator.generateId());
        Author updatedAuthor = authorRepository.save(author);

        if( updatedAuthor!=null ) {
            authorEventsPublisher.sendAuthorUpdated(updatedAuthor, desiredVersion);
        }

        return updatedAuthor;
    }

    @Override
    public Author partialUpdate(AuthorViewAMQP authorViewAMQP) {

        final var author = findByName(authorViewAMQP.getName()).get(0);

        final String name = authorViewAMQP.getName();
        final String bio = authorViewAMQP.getBio();
        final String photoURI = null;
        final MultipartFile photo = null;
        final UpdateAuthorRequest authorReq = new UpdateAuthorRequest(bio, name, photo, photoURI);
        author.applyPatch(authorViewAMQP.getVersion(), authorReq);
        return authorRepository.save(author);
    }

    @Override
    public Optional<Author> removeAuthorPhoto(String authorNumber, long desiredVersion) {
        Author author = authorRepository.findByAuthorNumber(authorNumber)
                .orElseThrow(() -> new NotFoundException("Cannot find reader"));

        String photoFile = author.getPhoto().getPhotoFile();
        author.removePhoto(desiredVersion);
        Optional<Author> updatedAuthor = Optional.of(authorRepository.save(author));
        photoRepository.deleteByPhotoFile(photoFile);
        return updatedAuthor;
    }

    protected Author fromAuthorTempEntitytoAuthor(AuthorTempEntity authorTempEntity) {

        Author author = new Author(
                authorTempEntity.getName().getName(),
                authorTempEntity.getBio().getBio(),
                null
        );
        return author;
    }

}