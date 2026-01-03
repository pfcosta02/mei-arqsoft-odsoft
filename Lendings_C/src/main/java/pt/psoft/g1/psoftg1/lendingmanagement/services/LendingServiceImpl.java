package pt.psoft.g1.psoftg1.lendingmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.exceptions.LendingForbiddenException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingCommandDTO;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingViewAMQP;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.LendingOutboxRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingOutbox;
import pt.psoft.g1.psoftg1.lendingmanagement.publishers.LendingEventPublisher;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;
import pt.psoft.g1.psoftg1.shared.model.LendingEvents;

import java.time.LocalDate;
import java.time.Year;


@Service
@RequiredArgsConstructor
@PropertySource({"classpath:config/library.properties"})
@Slf4j
public class LendingServiceImpl implements LendingService{

    @Value("${lendingDurationInDays}")
    private int lendingDurationInDays;
    @Value("${fineValuePerDayInCents}")
    private int fineValuePerDayInCents;

    private final LendingRepository lendingRepository;
    private final LendingOutboxRepository outboxRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final ObjectMapper objectMapper;
    private final LendingEntityMapper lendingEntityMapper;
    private final EntityManager em;

    @Override
    @Transactional
    public Lending createLending(LendingCommandDTO dto) {
        try {
            log.info("[Command] Creating lending for reader: {} and book: {}", dto.getReaderNumber(), dto.getBookIsbn());

            // 1. Busca o Book pelo ISBN
            Book book = bookRepository.findByIsbn(dto.getBookIsbn())
                    .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + dto.getBookIsbn()));

            // 2. Busca o ReaderDetails pelo reader number
            ReaderDetails readerDetails = readerRepository.findByReaderNumber(dto.getReaderNumber())
                    .orElseThrow(() -> new RuntimeException("Reader not found with number: " + dto.getReaderNumber()));

            // 3. ✅ CORRIGIDO: Gera o próximo seq do ano
            int currentYear = Year.now().getValue();
            int nextSeq = lendingRepository.getCountFromCurrentYear() + 1;

            log.debug("[Command] Creating lending with year: {}, seq: {}", currentYear, nextSeq);

            // 4. Cria o Lending
            Lending lending = new Lending(
                    book,
                    readerDetails,
                    nextSeq,  // ✅ CORRIGIDO: Usa o próximo seq
                    lendingDurationInDays,
                    fineValuePerDayInCents
            );

            // 5. Guarda no repositório
            Lending saved = lendingRepository.save(lending);

            // 6. Cria evento e guarda em Outbox (MESMA TRANSAÇÃO)
            LendingEventAMQP event = LendingEventAMQP.from(saved);
            String payload = objectMapper.writeValueAsString(event);
            LendingOutbox outbox = new LendingOutbox(
                    saved.getLendingNumber(),
                    LendingEvents.LENDING_CREATED,
                    payload
            );
            outboxRepository.save(outbox);

            log.info("[Command] Lending created: {} with outbox event", saved.getLendingNumber());
            return saved;
        } catch (Exception e) {
            log.error("[Command] Error creating lending: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating lending: " + e.getMessage(), e);
        }
    }




    @Override
    @Transactional
    public Lending returnLending(String lendingNumber, String commentary, Integer rating, Long expectedVersion) {
        try {
            log.info("[Command] Returning lending: {}", lendingNumber);

            // 1. Carrega para obter versão atual
            var entityOpt = lendingRepository.findByLendingNumber(lendingNumber);
            var existingEntity = entityOpt.orElseThrow(() -> new RuntimeException("Lending not found: " + lendingNumber));

            long versionToUse = (expectedVersion != null) ? expectedVersion : existingEntity.getVersion();

            // 2. ⭐ Usa o novo método que faz UPDATE + REFRESH
            Lending updated = lendingRepository.returnLendingAndGetUpdated(
                    lendingNumber,
                    LocalDate.now(),
                    commentary,
                    rating,
                    versionToUse
            );

            log.debug("[Command] After update - commentary='{}' rating={}",
                    updated.getCommentary(), updated.getRating());

            // 3. Publica evento com dados corretos
            var event = LendingEventAMQP.from(updated);
            var payload = objectMapper.writeValueAsString(event);
            outboxRepository.save(new LendingOutbox(
                    updated.getLendingNumber(),
                    LendingEvents.LENDING_RETURNED,
                    payload
            ));

            log.info("[Command] Lending returned: {} with outbox event", lendingNumber);
            return updated;
        } catch (Exception e) {
            log.error("[Command] Error returning lending: {}", e.getMessage(), e);
            throw new RuntimeException("Error returning lending: " + e.getMessage(), e);
        }
    }


    @Override
    @Transactional
    public void deleteLending(String lendingNumber) {
        try {
            log.info("[Command] Deleting lending: {}", lendingNumber);

            // 1. Busca o lending
            Lending lending = lendingRepository.findByLendingNumber(lendingNumber)
                    .orElseThrow(() -> new RuntimeException("Lending not found: " + lendingNumber));

            // 2. Deleta
            lendingRepository.delete(lending);

            // 3. Publica evento em Outbox
            LendingEventAMQP event = LendingEventAMQP.from(lending);
            String payload = objectMapper.writeValueAsString(event);
            LendingOutbox outbox = new LendingOutbox(
                    lending.getLendingNumber(),
                    LendingEvents.LENDING_DELETED,
                    payload
            );
            outboxRepository.save(outbox);

            log.info("[Command] Lending deleted: {} with outbox event", lendingNumber);
        } catch (Exception e) {
            log.error("[Command] Error deleting lending: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting lending: " + e.getMessage(), e);
        }
    }
}