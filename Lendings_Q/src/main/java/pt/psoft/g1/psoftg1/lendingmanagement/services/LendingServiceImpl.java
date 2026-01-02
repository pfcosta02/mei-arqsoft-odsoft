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
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingOutbox;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingNumberEntity;
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

    private final LendingRepository lendingRepository;
    private final LendingEntityMapper lendingEntityMapper;


    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;


    @Override
    @Transactional
    public void createFromEvent(LendingEventAMQP event) {
        log.debug("[Query] Event received for lending created: {}", event.lendingNumber);

        var book = bookRepository.findByIsbn(event.bookIsbn)
                .orElseThrow(() -> new IllegalStateException("Book not found in Query DB: " + event.bookIsbn));

        var readerDetails = readerRepository.findByReaderNumber(event.readerNumber)
                .orElseThrow(() -> new IllegalStateException("Reader not found in Query DB: " + event.readerNumber));

        var lending = new Lending(
                book,
                readerDetails,
                event.lendingNumber,
                event.startDate,
                event.limitDate,
                event.fineValuePerDayInCents
        );

        // se o evento trouxer returnedDate/version, opcionalmente ajusta:
        if (event.returnedDate != null) {
            // Seta returnedDate diretamente no domínio (se tiver setter ou construtor)
            // ou usa um método apropriado; aqui simplifico:
            var returned = event.returnedDate;
            // se não tiver setter público: cria um novo Lending builder com returnedDate
            lending = Lending.builder()
                    .book(book)
                    .readerDetails(readerDetails)
                    .lendingNumber(new pt.psoft.g1.psoftg1.lendingmanagement.model.LendingNumber(event.lendingNumber))
                    .startDate(event.startDate)
                    .limitDate(event.limitDate)
                    .returnedDate(returned)
                    .fineValuePerDayInCents(event.fineValuePerDayInCents)
                    .build();
        }

        lendingRepository.save(lending);
        log.info("[Query] Acknowledged & persisted lending created: {}", event.lendingNumber);
    }

    @Override


    @Transactional
    public void updateFromEvent(LendingEventAMQP event) {
        log.debug("[Query] Event received for lending updated: {}", event.lendingNumber);

        // 1) Se não existe, opcionalmente faz upsert (cria registo a partir do evento 'created')
        var existingOpt = lendingRepository.findByLendingNumber(event.lendingNumber);
        if (existingOpt.isEmpty()) {
            log.warn("[Query] Lending {} não existe na projeção; vou criar antes de atualizar.", event.lendingNumber);

            // upsert básico (criar com os campos do evento)
            var created = new LendingEntity(
                    // book, readerDetails: se não tiveres relações no read-model, podes manter null
                    /* book */ null,
                    /* readerDetails */ null,
                    /* lendingNumber embeddable */ new LendingNumberEntity(event.lendingNumber), // adapta ao teu embeddable
                    java.time.LocalDate.parse(event.startDate.toString()),
                    java.time.LocalDate.parse(event.limitDate.toString()),
                    (event.returnedDate != null) ? java.time.LocalDate.parse(event.returnedDate.toString()) : null,
                    event.fineValuePerDayInCents,
                    event.commentary,
                    event.rating
            );


            Lending lending_created = lendingEntityMapper.toModel(created);

            lendingRepository.save(lending_created);
        }

        // 2) Aplica o UPDATE de 'returned'
        int rows = lendingRepository.markReturned(
                event.lendingNumber,
                (event.returnedDate != null) ? event.returnedDate : java.time.LocalDate.now(),
                event.commentary,
                event.rating,
                event.version
        );

        if (rows == 0) {
            log.error("[Query] markReturned não atualizou nenhuma linha para {}", event.lendingNumber);
            // opcional: lançar exceção ou deixar apenas log
        } else {
            log.info("[Query] Acknowledged & persisted lending updated: {}", event.lendingNumber);
        }
    }


    @Override
    @Transactional
    public void deleteFromEvent(LendingEventAMQP event) {
        log.debug("[Query] Event received for lending deleted: {}", event.lendingNumber);

        var existing = lendingRepository.findByLendingNumber(event.lendingNumber)
                .orElseThrow(() -> new RuntimeException("Lending not found to delete: " + event.lendingNumber));

        lendingRepository.delete(existing);
        log.info("[Query] Acknowledged & persisted lending deleted: {}", event.lendingNumber);
    }



@Override
    @Transactional(readOnly = true)
    public List<LendingQueryDTO> getAllLendings() {
        log.debug("[Query] Fetching all lendings");
        return lendingRepository.findAll().stream()
                .map(LendingQueryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LendingQueryDTO getLendingByNumber(String lendingNumber) {
        log.debug("[Query] Fetching lending: {}", lendingNumber);
        return lendingRepository.findByLendingNumber(lendingNumber)
                .map(LendingQueryDTO::from)
                .orElseThrow(() -> new RuntimeException("Lending not found: " + lendingNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LendingQueryDTO> getLendingsByReader(String readerNumber) {
        log.debug("[Query] Fetching lendings by reader: {}", readerNumber);
        return lendingRepository.findByReaderNumber(readerNumber).stream()
                .map(LendingQueryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LendingQueryDTO> getActiveLendingsByReader(String readerNumber) {
        log.debug("[Query] Fetching active lendings by reader: {}", readerNumber);
        return lendingRepository.findActiveLendingsByReader(readerNumber).stream()
                .map(LendingQueryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LendingQueryDTO> getOverdueLendings() {
        log.debug("[Query] Fetching overdue lendings");
        return lendingRepository.findOverdueLendings().stream()
                .map(LendingQueryDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public int countLendingsCurrentYear() {
        log.debug("[Query] Counting lendings current year");
        return lendingRepository.getCountFromCurrentYear();
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageLendingDuration() {
        log.debug("[Query] Getting average lending duration");
        return lendingRepository.getAverageDuration();
    }

    // ========== SINCRONIZAÇÃO ==========
    // ✅ CORRIGIDO: Query sincroniza via BD compartilhada
    // Não precisa fazer nada aqui porque Command e Query usam a mesma BD
    // Os eventos são apenas para notificação

//    @Override
//    @Transactional
//    public void createFromEvent(LendingEventAMQP event) {
//        log.debug("[Query] Event received for lending created: {}", event.lendingNumber);
//        // Query já vê via BD compartilhada, apenas log
//        log.info("[Query] Acknowledged lending created: {}", event.lendingNumber);
//    }
//
//    @Override
//    @Transactional
//    public void updateFromEvent(LendingEventAMQP event) {
//        log.debug("[Query] Event received for lending updated: {}", event.lendingNumber);
//        // Query já vê via BD compartilhada, apenas log
//        log.info("[Query] Acknowledged lending updated: {}", event.lendingNumber);
//    }
//
//    @Override
//    @Transactional
//    public void deleteFromEvent(LendingEventAMQP event) {
//        log.debug("[Query] Event received for lending deleted: {}", event.lendingNumber);
//        // Query já vê via BD compartilhada, apenas log
//        log.info("[Query] Acknowledged lending deleted: {}", event.lendingNumber);
//    }
}