package pt.psoft.g1.psoftg1.readermanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import pt.psoft.g1.psoftg1.featuremanagement.services.FeatureService;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.services.CreateReaderRequest;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.readermanagement.services.UpdateReaderRequest;
import pt.psoft.g1.psoftg1.shared.api.ListResponse;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
import pt.psoft.g1.psoftg1.shared.services.SearchRequest;
import pt.psoft.g1.psoftg1.usermanagement.Role;


@Tag(name = "Readers", description = "Endpoints to manage readers")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readers")
class ReaderController {
    private final ReaderService readerService;
    private final ReaderViewMapper readerViewMapper;
    private final LendingService lendingService;
    //TODO
    // private final LendingViewMapper lendingViewMapper;
    private final ConcurrencyService concurrencyService;
    private final FileStorageService fileStorageService;

    private final FeatureService featureService;

    @Operation(summary = "Creates a reader and respective user")
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<ReaderViewAMQP> createReaderAndUser(
            @Valid @RequestBody CreateReaderRequest readerRequest,
            Authentication authentication) throws ValidationException
    {
        // Obter userId do utilizador autenticado (quem está logado)
        // JWT subject format: "id,username" - extrair apenas username
        String userId = "anonymous";
        if (authentication != null && authentication.getName() != null)
        {
            String[] parts = authentication.getName().split(",");
            userId = parts.length > 1 ? parts[1] : parts[0]; // username é a segunda parte
        }

        // DARK LAUNCH: Executa feature invisível para coletar métricas
        if (featureService.shouldExecuteInDarkLaunch(userId, "createReaderAdvanced")) {
            // Validações avançadas que não afetam o fluxo normal
            String email = readerRequest.getUsername();
            String phone = readerRequest.getPhoneNumber();

            // Validação 1: Email tem domínio válido
            boolean hasValidDomain = email != null &&
                    (email.endsWith("@gmail.com") || email.endsWith("@email.com"));

            // Validação 2: Telefone tem exatamente 9 dígitos
            boolean hasValidPhone = phone != null && phone.matches("\\d{9}");

            // Registar métricas adicionais
            if (hasValidDomain) {
                featureService.getDarkLaunchMetrics()
                        .computeIfAbsent("validDomainEmails", k -> new java.util.concurrent.atomic.AtomicLong(0))
                        .incrementAndGet();
            }
            if (hasValidPhone) {
                featureService.getDarkLaunchMetrics()
                        .computeIfAbsent("valid9DigitPhones", k -> new java.util.concurrent.atomic.AtomicLong(0))
                        .incrementAndGet();
            }
        }

        // Feature flag normal: controla se user autenticado pode usar feature
        if (!featureService.isFeatureEnabledForUser(userId))
        {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Feature not available for your user"
            );
        }

        MultipartFile file = readerRequest.getPhoto();

        String fileName = fileStorageService.getRequestPhoto(file);

        ReaderDetails readerDetails = readerService.create(readerRequest, fileName);

        // Retornar 202
        return ResponseEntity.accepted()
                .eTag(Long.toString(readerDetails.getVersion()))
                .body(readerViewMapper.toReaderViewAMQP(readerDetails));

    }

    @Operation(summary = "Deletes a reader photo")
    @DeleteMapping("/photo")
    public ResponseEntity<Void> deleteReaderPhoto(Authentication authentication) {
        // TODO> Como fazer isto da autenticacao?
        // User loggedUser = userService.getAuthenticatedUser(authentication);
        // Optional<ReaderDetails> optReaderDetails = readerService.findByUsername(loggedUser.getUsername());
        // if(optReaderDetails.isEmpty()) {
        //     throw new AccessDeniedException("Could not find a valid reader from current auth");
        // }

        // ReaderDetails readerDetails = optReaderDetails.get();

        // if(readerDetails.getPhoto() == null) {
        //     throw new NotFoundException("Reader has no photo to delete");
        // }

        // this.fileStorageService.deleteFile(readerDetails.getPhoto().getPhotoFile());
        // readerService.removeReaderPhoto(readerDetails.getReaderNumber(), readerDetails.getVersion());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Updates a reader")
    @RolesAllowed(Role.READER)
    @PatchMapping
    public ResponseEntity<ReaderView> updateReader(
            @Valid UpdateReaderRequest readerRequest,
            Authentication authentication,
            final WebRequest request) {

        final String ifMatchValue = request.getHeader(ConcurrencyService.IF_MATCH);
        if (ifMatchValue == null || ifMatchValue.isEmpty() || ifMatchValue.equals("null")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You must issue a conditional PATCH using 'if-match'");
        }

        MultipartFile file = readerRequest.getPhoto();

        String fileName = this.fileStorageService.getRequestPhoto(file);

        // TODO> Como fazer isto da autenticacao?
        // User loggedUser = userService.getAuthenticatedUser(authentication);
        // ReaderDetails readerDetails = readerService
        //         .update(loggedUser.getId(), readerRequest, concurrencyService.getVersionFromIfMatchHeader(ifMatchValue), fileName);

        // return ResponseEntity.ok()
        //         .eTag(Long.toString(readerDetails.getVersion()))
        //         .body(readerViewMapper.toReaderView(readerDetails));
        return null;
    }

    @PostMapping("/search")
    public ListResponse<ReaderView> searchReaders(
            @RequestBody final SearchRequest<SearchReadersQuery> request) {
        final var readerList = readerService.searchReaders(request.getPage(), request.getQuery());
        return new ListResponse<>(readerViewMapper.toReaderView(readerList));
    }
}