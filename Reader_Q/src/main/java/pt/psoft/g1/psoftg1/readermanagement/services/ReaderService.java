package pt.psoft.g1.psoftg1.readermanagement.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;

public interface ReaderService {
    void createEvent(ReaderDetailsDTO rd);
    void updateEvent(ReaderDetailsDTO rd);
    void deleteEvent(String readerId);

    List<ReaderBookCountDTO> findTopByGenre(String genre, LocalDate startDate, LocalDate endDate);
    Optional<ReaderDetails> findByReaderNumber(String readerNumber);
    List<ReaderDetails> findByPhoneNumber(String phoneNumber);
    Optional<ReaderDetails> findByEmail(final String email);
    Iterable<ReaderDetails> findAll();
    List<ReaderDetails> findTopReaders(int minTop);
    List<Reader> searchByName(String namePart);
}
