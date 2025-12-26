package pt.psoft.g1.psoftg1.readermanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
import pt.psoft.g1.psoftg1.readermanagement.dto.*;
import pt.psoft.g1.psoftg1.readermanagement.model.*;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.readermanagement.publishers.ReaderEventsPublisher;
import pt.psoft.g1.psoftg1.readermanagement.repositories.*;
import pt.psoft.g1.psoftg1.shared.model.ReaderEvents;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {
    /* ReaderDetails */
    private final ReaderDetailsRepository readerDetailsRepo;
    private final ReaderDetailsTempRepository readerDetailsTempRepo;

    /* ReaderRepo */
    private final ReaderRepository readerRepo;
    private final ReaderTempRepository readerTempRepo;

    private final ReaderMapper readerMapper;
    private final ForbiddenNameRepository forbiddenNameRepository;
    private final PhotoRepository photoRepository;
    private final IdGenerator idGenerator;
    private final ReaderEventsPublisher publisher;

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public ReaderDetails create(CreateReaderRequest request, String photoURI)
    {
        if (readerRepo.findByEmail(request.getUsername()).isPresent() || readerTempRepo.findByEmail(request.getUsername()).isPresent())
        {
            throw new ConflictException("Reader with username " + request.getUsername() + " already exist");
        }

        Iterable<String> words = List.of(request.getFullName().split("\\s+"));
        for (String word : words)
        {
            if(!forbiddenNameRepository.findByForbiddenNameIsContained(word).isEmpty())
            {
                throw new IllegalArgumentException("Name contains a forbidden word");
            }
        }

        // TODO:Para ja estou a assumir que o user nao existe, mas seria boa pratica antes confirmar
        List<String> stringInterestList = request.getInterestList();
        List<String> interestList = null;

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
        if(photo == null && photoURI != null || photo != null && photoURI == null) {
            request.setPhoto(null);
        }

        /* Neste ponto a string do userId é nula porque ainda nao foi criado */
        Reader reader = new Reader(idGenerator.generateId(), "", request.getFullName(), request.getUsername());
        readerTempRepo.save(reader);

        /* Persistir o reader temporariamente */
        ReaderDetails readerDetails = readerMapper.createReaderDetails(0, reader, request, photoURI, interestList);

        readerDetails.setId(idGenerator.generateId());
        ReaderDetails readerDetailsSaved = readerDetailsTempRepo.save(readerDetails);

        // Em vez de publicar diretamente, gravamos no Outbox
        try {
            UserDTO dto = new UserDTO("", reader.getReaderId(), request.getUsername(), request.getPassword(), request.getFullName(), 0L);

            String payload = objectMapper.writeValueAsString(dto);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(reader.getReaderId());
            event.setEventType(ReaderEvents.TEMP_READER_CREATED);
            event.setPayload(payload);
            event.setStatus(OutboxEnum.NEW);

            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar evento Outbox", e);
        }

        return readerDetailsSaved;
    }

    @Override
    public ReaderDetails update(final String id, final UpdateReaderRequest request, final long desiredVersion, String photoURI)
    {
        final ReaderDetails readerDetails = readerDetailsRepo.findByUserId(id)
                .orElseThrow(() -> new NotFoundException("Cannot find reader"));

        List<String> stringInterestList = request.getInterestList();
        List<String> interestList = null;
        // TODO> Tentar entender o que preciso de fazer aqui
        // List<Genre> interestList = this.getGenreListFromStringList(stringInterestList);

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
        if(photo == null && photoURI != null || photo != null && photoURI == null) {
            request.setPhoto(null);
        }

        readerDetails.applyPatch(desiredVersion, request, photoURI, interestList);

        readerRepo.save(readerDetails.getReader());

        ReaderDetails readerDetailsUpdated = readerDetailsRepo.save(readerDetails);

        // Evento READER_UPDATED: informa o Query para update também
        try
        {
            ReaderDetailsDTO dto = toReaderDetailsDTO(readerDetailsUpdated);
            String payload = objectMapper.writeValueAsString(dto);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(readerDetailsUpdated.getId());
            event.setEventType(ReaderEvents.READER_UPDATED);
            event.setPayload(payload);
            event.setStatus(OutboxEnum.NEW);
            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar evento READER_PERSISTED no Outbox", e);
        }

        return readerDetailsUpdated;
    }

    private List<String> getGenreListFromStringList(List<String> interestList) {
        if(interestList == null) {
            return null;
        }

        if(interestList.isEmpty()) {
            return new ArrayList<>();
        }

        // TODO
        // List<Genre> genreList = new ArrayList<>();
        // for(String interest : interestList) {
        //     Optional<Genre> optGenre = genreRepo.findByString(interest);
        //     if(optGenre.isEmpty()) {
        //         throw new NotFoundException("Could not find genre with name " + interest);
        //     }

        //     genreList.add(optGenre.get());
        // }

        // return genreList;
        return List.of(null);
    }

    @Override
    public Optional<ReaderDetails> removeReaderPhoto(String readerNumber, long desiredVersion) {
        ReaderDetails readerDetails = readerDetailsRepo.findByReaderNumber(readerNumber)
                .orElseThrow(() -> new NotFoundException("Cannot find reader"));

        String photoFile = readerDetails.getPhoto().getPhotoFile();
        readerDetails.removePhoto(desiredVersion);
        Optional<ReaderDetails> updatedReader = Optional.of(readerDetailsRepo.save(readerDetails));
        photoRepository.deleteByPhotoFile(photoFile);
        return updatedReader;
    }

    @Override
    public List<ReaderDetails> searchReaders(pt.psoft.g1.psoftg1.shared.services.Page page, SearchReadersQuery query) {
        if (page == null)
            page = new pt.psoft.g1.psoftg1.shared.services.Page(1, 10);

        if (query == null)
            query = new SearchReadersQuery("", "","");

        final var list = readerDetailsRepo.searchReaderDetails(page, query);

        if(list.isEmpty())
            throw new NotFoundException("No results match the search query");

        return list;
    }

    @Override
    @Transactional
    public void persistTemporary(UserDTO userDTO)
    {
        Reader readerToPresist = readerTempRepo.findByReaderId(userDTO.readerId)
                .orElseThrow(() -> new NotFoundException("Nao ha nenhum reader temporario com o id:" + userDTO.readerId));

        /* Armazenar o userId correspondente */
        readerToPresist.setUserId(userDTO.id);

        /* Primeiro persistimos o Reader */
        Reader savedReader = readerRepo.save(readerToPresist);

        ReaderDetails readerDetailsToPresist = readerDetailsTempRepo.findByReaderId(userDTO.readerId)
                .orElseThrow(() -> new NotFoundException("Algo estranho aconteceu e o ReaderDetails nao existe"));

        /* Colocar o Reader no Reader Details */
        readerDetailsToPresist.setReader(savedReader);

        /* Criar apos o ultimo Reader Number existente */
        /* Neste caso isto foi preciso porque os Counters do Repo Temporario e Permanente podem diferir */
        int count = readerDetailsRepo.getCountFromCurrentYear();
        readerDetailsToPresist.setReaderNumber(count + 1);

        /* Persistir o Reader Details */
        ReaderDetails savedReaderDetails = readerDetailsRepo.save(readerDetailsToPresist);

        /* Eliminar o Reader Details temporario */
        readerDetailsTempRepo.delete(readerDetailsToPresist.getId());

        /* Depois apagamos o Reader do repositorio temporario */
        readerTempRepo.delete(userDTO.readerId);

        // Evento READER_PERSISTED: informa o AuthNUsers para persistir também
        try
        {
            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(userDTO.id);
            event.setEventType(ReaderEvents.TEMP_READER_PERSISTED);
            event.setPayload(userDTO.id); // Apenas o userId
            event.setStatus(OutboxEnum.NEW);
            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar evento READER_PERSISTED no Outbox", e);
        }

        // Evento READER_CREATED: informa o Query para criar também
        try
        {
            ReaderDetailsDTO dto = toReaderDetailsDTO(savedReaderDetails);

            String payload = objectMapper.writeValueAsString(dto);

            OutboxEvent event = new OutboxEvent();
            event.setAggregateId(savedReaderDetails.getId());
            event.setEventType(ReaderEvents.READER_CREATED);
            event.setPayload(payload);
            event.setStatus(OutboxEnum.NEW);
            outboxEventRepository.save(event);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Erro ao salvar evento READER_CREATED no Outbox", e);
        }
    }

    @Override
    @Transactional
    public List<ReaderDetailsDTO> readersToDTO()
    {
        List<ReaderDetails> rds = readerDetailsRepo.findAll();
        List<ReaderDetailsDTO> rdDtos = new ArrayList<>();

        for (ReaderDetails rd : rds)
        {
            ReaderDTO readerDTO = new ReaderDTO(rd.getReader().getReaderId(), rd.getReader().getUserId(),
                    new NameDTO(rd.getReader().getName().getName()), rd.getReader().getEmail());

            ReaderDetailsDTO dto = new ReaderDetailsDTO(
                    rd.getId(),
                    readerDTO,
                    new ReaderNumber(rd.getReaderNumber()), // ReaderNumber
                    new BirthDateDTO(rd.getBirthDate().toString()), // BirthDateDTO
                    new PhoneNumber(rd.getPhoneNumber()), // PhoneNumber
                    rd.isGdprConsent(),
                    rd.isMarketingConsent(),
                    rd.isThirdPartySharingConsent(),
                    rd.getVersion(),
                    rd.getInterestList(), // List<String>
                    rd.getPhoto() != null ? rd.getPhoto().getPhotoFile() : null // String photo
            );

            rdDtos.add(dto);
        }

        return rdDtos;
    }

    private ReaderDetailsDTO toReaderDetailsDTO(ReaderDetails rd)
    {
        ReaderDTO readerDTO = new ReaderDTO(rd.getReader().getReaderId(), rd.getReader().getUserId(),
                new NameDTO(rd.getReader().getName().getName()), rd.getReader().getEmail());

        ReaderDetailsDTO dto = new ReaderDetailsDTO(
                rd.getId(),
                readerDTO,
                new ReaderNumber(rd.getReaderNumber()), // ReaderNumber
                new BirthDateDTO(rd.getBirthDate().toString()), // BirthDateDTO
                new PhoneNumber(rd.getPhoneNumber()), // PhoneNumber
                rd.isGdprConsent(),
                rd.isMarketingConsent(),
                rd.isThirdPartySharingConsent(),
                rd.getVersion(),
                rd.getInterestList(), // List<String>
                rd.getPhoto() != null ? rd.getPhoto().getPhotoFile() : null // String photo
        );

        return dto;
    }
}