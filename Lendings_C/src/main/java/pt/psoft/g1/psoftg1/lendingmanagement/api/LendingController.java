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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.services.CreateLendingRequest;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.lendingmanagement.services.SearchLendingQuery;
import pt.psoft.g1.psoftg1.lendingmanagement.services.SetLendingReturnedRequest;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.shared.api.ListResponse;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.shared.services.SearchRequest;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.util.List;
import java.util.Objects;
@RestController
@RequestMapping("/api/lendings")
@RequiredArgsConstructor
@Slf4j
public class LendingController {

    private final LendingService lendingService;

    private final LendingViewMapper lendingViewMapper;

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

    @PutMapping("/{year}/{seq}")
    public ResponseEntity<LendingView> returnLending(
            @PathVariable int year,
            @PathVariable int seq,
            @RequestParam(required = false) String commentary) {

        String lendingNumber = year + "/" + seq;
        log.info("Returning lending: {}", lendingNumber);

        Lending lending = lendingService.returnLending(lendingNumber, commentary);

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