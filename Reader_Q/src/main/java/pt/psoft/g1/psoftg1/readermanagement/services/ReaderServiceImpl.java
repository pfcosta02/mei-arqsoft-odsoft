package pt.psoft.g1.psoftg1.readermanagement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderDetailsRepository;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;

@Service
@RequiredArgsConstructor
public class ReaderServiceImpl implements ReaderService {
    private final ReaderDetailsRepository readerDetailsRepo;

    /* ReaderRepo */
    private final ReaderRepository readerRepo;

    private final ReaderMapper readerMapper;
    private final ForbiddenNameRepository forbiddenNameRepository;
    private final PhotoRepository photoRepository;

    @Override
    public List<ReaderBookCountDTO> findTopByGenre(String genre, LocalDate startDate, LocalDate endDate){
        if(startDate.isAfter(endDate)){
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        Pageable pageableRules = PageRequest.of(0,5);
        return this.readerDetailsRepo.findTopByGenre(pageableRules, genre, startDate, endDate);
    }

    @Override
    public Optional<ReaderDetails> findByReaderNumber(String readerNumber) {
        return this.readerDetailsRepo.findByReaderNumber(readerNumber);
    }

    @Override
    public List<ReaderDetails> findByPhoneNumber(String phoneNumber) {
        return this.readerDetailsRepo.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<ReaderDetails> findByEmail(final String email) {
        return this.readerDetailsRepo.findByEmail(email);
    }


    @Override
    public Iterable<ReaderDetails> findAll()
    {
        return this.readerDetailsRepo.findAll();
    }

    @Override
    public List<ReaderDetails> findTopReaders(int minTop) {
        if(minTop < 1) {
            throw new IllegalArgumentException("Minimum top reader must be greater than 0");
        }

        Pageable pageableRules = PageRequest.of(0,minTop);

        return readerDetailsRepo.findTopReaders(pageableRules);
    }

    @Override
    public List<Reader> searchByName(String namePart)
    {
        return readerRepo.searchByName(namePart);
    }

    @Override
    public void createEvent(ReaderDetailsDTO rd)
    {
        Reader reader = new Reader(rd.reader.readerId, rd.reader.userId, rd.reader.name.name, rd.reader.email);

        ReaderDetails readerDetails = new ReaderDetails(rd.readerNumber, reader, new BirthDate(rd.birthDate.birthDate), rd.phoneNumber,
                rd.gdprConsent, rd.marketingConsent, rd.thirdPartySharingConsent,
                rd.photo, rd.interestList);

        readerDetails.setId(rd.id);
        readerDetails.setVersion(rd.version);

        /* Primeiro persistimos o reader */
        readerRepo.save(reader);

        /* Agora persistimos os detalhes dele */
        readerDetailsRepo.save(readerDetails);
    }

    @Override
    public void updateEvent(ReaderDetailsDTO rd)
    {
        Reader reader = new Reader(rd.reader.readerId, rd.reader.userId, rd.reader.name.name, rd.reader.email);

        ReaderDetails readerDetails = new ReaderDetails(rd.readerNumber, reader, new BirthDate(rd.birthDate.birthDate), rd.phoneNumber,
                rd.gdprConsent, rd.marketingConsent, rd.thirdPartySharingConsent,
                rd.photo, rd.interestList);
        readerDetails.setId(rd.id);
        readerDetails.setVersion(rd.version);
    }

    @Override
    public void deleteEvent(String readerId)
    {
        ReaderDetails rd = readerDetailsRepo.findByReaderId(readerId)
                .orElseThrow(() -> new NotFoundException("Cannot find readerDetails with readerid:" + readerId));

        /* Eliminar primeiro o Reader Detail */
        readerDetailsRepo.delete(rd.getId());

        /* Eliminar o Reader */
        readerRepo.delete(readerId);
    }
}