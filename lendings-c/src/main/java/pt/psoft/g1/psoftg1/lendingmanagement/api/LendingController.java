package pt.psoft.g1.psoftg1.lendingmanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.psoft.g1.psoftg1.featuremanagement.services.FeatureService;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.shared.dtos.ReturnRequest;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/lendings")
@RequiredArgsConstructor
@Slf4j
public class LendingController {

    private final LendingService lendingService;
    private final LendingViewMapper lendingViewMapper;
    private final ConcurrencyService concurrencyService;
    private final FeatureService featureService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LendingView> createLending(@Valid @RequestBody LendingCommandDTO dto) {
        log.info("Creating lending for reader: {} and book: {}", dto.getReaderNumber(), dto.getBookIsbn());

        Lending lending = lendingService.createLending(dto);

        var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .pathSegment(lending.getLendingNumber())
                .build().toUri();

        return ResponseEntity.created(uri)
                .contentType(MediaType.parseMediaType("application/hal+json"))
                .eTag(Long.toString(lending.getVersion()))
                .body(lendingViewMapper.toLendingView(lending));
    }

    @PatchMapping("/{year}/{seq}")
    public ResponseEntity<LendingView> returnLending(
            final WebRequest request,
            @PathVariable int year,
            @PathVariable int seq,
            @Valid @RequestBody ReturnRequest body,
            Authentication authentication) {

        final String ifMatchValue = request.getHeader(ConcurrencyService.IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty() || ifMatchValue.equals("null")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }

        String lendingNumber = year + "/" + seq;
        log.info("Returning lending: {}", lendingNumber);

        // Obter userId do utilizador autenticado
        String userId = "anonymous";
        if (authentication != null && authentication.getName() != null) {
            String[] parts = authentication.getName().split(",");
            userId = parts.length > 1 ? parts[1] : parts[0];
        }

        // KILL SWITCH: Se ativado, feature é desativada para todos
        if (!featureService.isFeatureEnabledForUser(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Return lending feature not available at this moment"
            );
        }

        // DARK LAUNCH: Executa validações avançadas invisualmente para coletar métricas
        if (featureService.shouldExecuteInDarkLaunch(userId, "returnLendingAdvanced")) {
            // Validações avançadas que não afetam o fluxo normal
            String commentary = body.getCommentary();
            Integer rating = body.getRating();

            // Validação 1: Comentário tem mais de 10 caracteres
            boolean hasDetailedCommentary = commentary != null && commentary.length() > 10;

            // Validação 2: Rating é válido (1-5)
            boolean hasValidRating = rating != null && rating >= 1 && rating <= 5;

            // Registar métricas adicionais
            if (hasDetailedCommentary) {
                featureService.getDarkLaunchMetrics()
                        .computeIfAbsent("detailedCommentaries", k -> new AtomicLong(0))
                        .incrementAndGet();
            }
            if (hasValidRating) {
                featureService.getDarkLaunchMetrics()
                        .computeIfAbsent("validRatings", k -> new AtomicLong(0))
                        .incrementAndGet();
            }

            // Métrica: Total de returns em dark launch
            featureService.getDarkLaunchMetrics()
                    .computeIfAbsent("totalReturnsProcessed", k -> new AtomicLong(0))
                    .incrementAndGet();

            log.debug("[DARK LAUNCH] Advanced validations executed for lending return: {} (invisible to user)",
                    lendingNumber);
        }

        Long expectedVersion = concurrencyService.tryGetNumericVersionFromIfMatch(ifMatchValue);

        Lending lending = lendingService.returnLending(
                lendingNumber, body.getCommentary(), body.getRating(), expectedVersion);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/hal+json"))
                .eTag(Long.toString(lending.getVersion()))
                .body(lendingViewMapper.toLendingView(lending));
    }


    @DeleteMapping("/{year}/{seq}")
    public ResponseEntity<Void> deleteLending(
            @PathVariable int year,
            @PathVariable int seq) {

        String lendingNumber = year + "/" + seq;
        log.info("Deleting lending: {}", lendingNumber);

        lendingService.deleteLending(lendingNumber);

        return ResponseEntity.noContent().build();
    }
}