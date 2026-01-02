package pt.psoft.g1.psoftg1.lendingmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.exceptions.LendingForbiddenException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingCommandDTO;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingViewAMQP;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.LendingOutboxRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingOutbox;
import pt.psoft.g1.psoftg1.lendingmanagement.publishers.LendingEventPublisher;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;
import pt.psoft.g1.psoftg1.shared.model.LendingEvents;


@Service
@RequiredArgsConstructor
@PropertySource({"classpath:config/library.properties"})
public class LendingServiceImpl implements LendingService{

    private final LendingRepository repository;
    private final LendingOutboxRepository outboxRepository;
    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void createLending(LendingCommandDTO dto) {
        try {
            // 1. Busca Book e ReaderDetails
            var book = bookRepository.findByIsbn(dto.getBookIsbn())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            var readerDetails = readerRepository.findByUserId(dto.getReaderId())
                    .orElseThrow(() -> new RuntimeException("Reader not found"));

            // 2. Cria entidade Lending
            Lending lending = new Lending(
                    book,
                    readerDetails,
                    1,  // seq - ajusta conforme necessário
                    dto.getLendingDurationDays() != null ? dto.getLendingDurationDays() : 14,
                    dto.getFineValuePerDayInCents() != null ? dto.getFineValuePerDayInCents() : 50
            );

            // 3. Guarda Lending na BD
            Lending saved = repository.save(lending);

            // 4. Cria evento e guarda em Outbox (MESMA TRANSAÇÃO)
            LendingEventAMQP event = LendingEventAMQP.from(saved);
            String payload = objectMapper.writeValueAsString(event);
            LendingOutbox outbox = new LendingOutbox(
                    saved.getPk(),
                    LendingEvents.LENDING_CREATED,
                    payload
            );
            outboxRepository.save(outbox);
            // COMMIT da transação aqui (tudo junto ou nada)

            System.out.println("[Command] Lending created with outbox event queued");
        } catch (Exception e) {
            System.err.println("[Command] Error creating lending: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void updateLending(String id, LendingCommandDTO dto) {
        try {
            Lending lending = repository.findByLendingNumber(id)
                    .orElseThrow(() -> new RuntimeException("Lending not found"));

            // Atualiza campos (implementa conforme necessário)
            // lending.update(dto);

            Lending updated = repository.save(lending);

            // Guarda evento em Outbox
            LendingEventAMQP event = LendingEventAMQP.from(updated);
            String payload = objectMapper.writeValueAsString(event);
            LendingOutbox outbox = new LendingOutbox(
                    updated.getPk(),
                    LendingEvents.LENDING_UPDATED,
                    payload
            );
            outboxRepository.save(outbox);

            System.out.println("[Command] Lending updated with outbox event queued");
        } catch (Exception e) {
            System.err.println("[Command] Error updating lending: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void deleteLending(String id) {
        try {
            Lending lending = repository.findByLendingNumber(id)
                    .orElseThrow(() -> new RuntimeException("Lending not found"));

            repository.delete(lending);

            // Guarda evento em Outbox
            LendingEventAMQP event = LendingEventAMQP.from(lending);
            String payload = objectMapper.writeValueAsString(event);
            LendingOutbox outbox = new LendingOutbox(
                    lending.getPk(),
                    LendingEvents.LENDING_DELETED,
                    payload
            );
            outboxRepository.save(outbox);

            System.out.println("[Command] Lending deleted with outbox event queued");
        } catch (Exception e) {
            System.err.println("[Command] Error deleting lending: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void returnBook(String id, String commentary) {
        try {
            Lending lending = repository.findByLendingNumber(id)
                    .orElseThrow(() -> new RuntimeException("Lending not found"));

            lending.setReturned(lending.getVersion(), commentary);
            Lending updated = repository.save(lending);

            // Guarda evento em Outbox
            LendingEventAMQP event = LendingEventAMQP.from(updated);
            String payload = objectMapper.writeValueAsString(event);
            LendingOutbox outbox = new LendingOutbox(
                    updated.getPk(),
                    LendingEvents.LENDING_RETURNED,
                    payload
            );
            outboxRepository.save(outbox);

            System.out.println("[Command] Book returned with outbox event queued");
        } catch (Exception e) {
            System.err.println("[Command] Error returning book: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}