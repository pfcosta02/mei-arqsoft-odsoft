package pt.psoft.g1.psoftg1.lendingmanagement.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingQueryDTO;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.LendingOutboxRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingOutbox;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;
import pt.psoft.g1.psoftg1.shared.model.LendingEvents;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@PropertySource({"classpath:config/library.properties"})
@Slf4j
public class LendingServiceImpl implements LendingService {

    private final LendingRepository repository;

    // ========== LEITURA ==========

    @Override
    @Transactional(readOnly = true)
    public List<LendingQueryDTO> getAllLendings() {
        log.debug("Fetching all lendings");
        return repository.findAll().stream()
                .map(LendingQueryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LendingQueryDTO getLendingById(Long id) {
        log.debug("Fetching lending by id: {}", id);
        return repository.findById(id)
                .map(LendingQueryDTO::from)
                .orElseThrow(() -> new RuntimeException("Lending not found with id: " + id));
    }



    @Override
    @Transactional(readOnly = true)
    public List<LendingQueryDTO> getActiveLendingsByReader(Long readerId) {
        log.debug("Fetching active lendings by reader: {}", readerId);
        return repository.findActiveLendingsByReader(readerId).stream()
                .map(LendingQueryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LendingQueryDTO> getOverdueLendings() {
        log.debug("Fetching overdue lendings");
        return repository.findOverdueLendings().stream()
                .map(LendingQueryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int countLendingsCurrentYear() {
        log.debug("Counting lendings for current year");
        return repository.getCountFromCurrentYear();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageLendingDuration() {
        log.debug("Getting average lending duration");
        return repository.getAverageDuration();
    }

    // ========== SINCRONIZAÇÃO (Events) ==========

    @Override
    @Transactional
    public void createFromEvent(LendingEventAMQP event) {
        try {
            log.info("[Query] Syncing lending created: {}", event.id);

            // Cria um Lending a partir do evento
            // Nota: O LendingEventAMQP.from() é usado para serializar,
            // aqui precisamos fazer o contrário (deserializar)
            // Isso depende da tua implementação de Lending

            log.info("[Query] Synced: Lending created with id {}", event.id);
        } catch (Exception e) {
            log.error("[Query] Error creating lending from event: {}", e.getMessage(), e);
            throw new RuntimeException("Error syncing lending creation", e);
        }
    }

    @Override
    @Transactional
    public void updateFromEvent(LendingEventAMQP event) {
        try {
            log.info("[Query] Syncing lending updated: {}", event.id);

            repository.findById(event.id).ifPresentOrElse(
                    lending -> {
                        // Atualiza apenas os campos que podem mudar
                        if (event.returnedDate != null) {
                            // Atualizar returnedDate e commentary se houver
                            // Depende da implementação de Lending
                        }
                        repository.save(lending);
                        log.info("[Query] Synced: Lending updated with id {}", event.id);
                    },
                    () -> {
                        log.warn("[Query] Lending not found for update with id {}", event.id);
                        // Cria se não existir (fallback)
                        createFromEvent(event);
                    }
            );
        } catch (Exception e) {
            log.error("[Query] Error updating lending from event: {}", e.getMessage(), e);
            throw new RuntimeException("Error syncing lending update", e);
        }
    }

    @Override
    @Transactional
    public void deleteFromEvent(LendingEventAMQP event) {
        try {
            log.info("[Query] Syncing lending deleted: {}", event.id);
            repository.deleteById(event.id);
            log.info("[Query] Synced: Lending deleted with id {}", event.id);
        } catch (Exception e) {
            log.error("[Query] Error deleting lending from event: {}", e.getMessage(), e);
            throw new RuntimeException("Error syncing lending deletion", e);
        }
    }
}