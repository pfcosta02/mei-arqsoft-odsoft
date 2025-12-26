package pt.psoft.g1.psoftg1.readermanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.external.service.ApiNinjasService;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingView;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;

import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.shared.api.ListResponse;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;

import pt.psoft.g1.psoftg1.usermanagement.Role;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final ApiNinjasService apiNinjasService;

    @Operation(summary = "Gets the reader data if authenticated as Reader or all readers if authenticated as Librarian")
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            // Use the `array` property instead of `schema`
            array = @ArraySchema(schema = @Schema(implementation = ReaderView.class))) })
    @GetMapping
    public ResponseEntity<?> getData(Authentication authentication)
    {
        // TODO> Como fazer isto?
        // User loggedUser = userService.getAuthenticatedUser(authentication);

        // if (!(loggedUser.getAuthorities().contains(new Role(Role.LIBRARIAN))))
        // {
        //     ReaderDetails readerDetails = readerService.findByUsername(loggedUser.getUsername())
        //             .orElseThrow(() -> new NotFoundException(ReaderDetails.class, loggedUser.getUsername()));

        //     return ResponseEntity.ok().eTag(Long.toString(readerDetails.getVersion())).body(readerViewMapper.toReaderView(readerDetails));
        // }
        return ResponseEntity.ok().body(readerViewMapper.toReaderView(readerService.findAll()));
    }

    @Operation(summary = "Gets reader by number")
    @ApiResponse(description = "Success", responseCode = "200", content = { @Content(mediaType = "application/json",
            // Use the `array` property instead of `schema`
            array = @ArraySchema(schema = @Schema(implementation = ReaderView.class))) })
    @GetMapping(value="/{year}/{seq}")
    public ResponseEntity<ReaderQuoteView> findByReaderNumber(@PathVariable("year")
                                                              @Parameter(description = "The year of the Reader to find")
                                                              final Integer year,
                                                              @PathVariable("seq")
                                                              @Parameter(description = "The sequencial of the Reader to find")
                                                              final Integer seq) {
        String readerNumber = year+"/"+seq;
        final var readerDetails = readerService.findByReaderNumber(readerNumber)
                .orElseThrow(() -> new NotFoundException("Could not find reader from specified reader number"));

        var readerQuoteView = readerViewMapper.toReaderQuoteView(readerDetails);

        int birthYear = readerDetails.getBirthDate().getBirthDate().getYear();
        int birhMonth = readerDetails.getBirthDate().getBirthDate().getMonthValue();

        readerQuoteView.setQuote(apiNinjasService.getRandomEventFromYearMonth(birthYear, birhMonth));

        return ResponseEntity.ok()
                .eTag(Long.toString(readerDetails.getVersion()))
                .body(readerQuoteView);
    }

    @Operation(summary = "Gets a list of Readers by phoneNumber")
    @GetMapping(params = "phoneNumber")
    public ListResponse<ReaderView> findByPhoneNumber(@RequestParam(name = "phoneNumber", required = false) final String phoneNumber) {

        List<ReaderDetails> readerDetailsList  = readerService.findByPhoneNumber(phoneNumber);

        if(readerDetailsList.isEmpty()) {
            throw new NotFoundException(ReaderDetails.class, phoneNumber);
        }

        return new ListResponse<>(readerViewMapper.toReaderView(readerDetailsList));
    }

    @RolesAllowed(Role.LIBRARIAN)
    @GetMapping(params = "name")
    public ListResponse<ReaderView> findByReaderName(@RequestParam("name") final String name)
    {
        List<Reader> readersList = readerService.searchByName(name);

        List<ReaderDetails> readerDetailsList = new ArrayList<>();

        for (Reader reader : readersList)
        {
            Optional<ReaderDetails> readerDetail = readerService.findByEmail(reader.getEmail());
            if (readerDetail.isPresent())
            {
                readerDetailsList.add(readerDetail.get());
            }
        }

        if(readerDetailsList.isEmpty()) {
            throw new NotFoundException("Could not find reader with name: " + name);
        }

        return new ListResponse<>(readerViewMapper.toReaderView(readerDetailsList));
    }

    @Operation(summary= "Gets a reader photo")
    @GetMapping("/{year}/{seq}/photo")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getSpecificReaderPhoto(@PathVariable("year")
                                                         @Parameter(description = "The year of the Reader to find")
                                                         final Integer year,
                                                         @PathVariable("seq")
                                                         @Parameter(description = "The sequencial of the Reader to find")
                                                         final Integer seq,
                                                         Authentication authentication)
    {
        // TODO> Como fazer isto da autenticacao?
        //     User loggedUser = userService.getAuthenticatedUser(authentication);
        //     System.out.println("Name: " + loggedUser.getUsername());
        //     System.out.println("Authorities: " + loggedUser.getAuthorities());

        //     //if Librarian is logged in, skip ahead
        //     if (!(loggedUser.getAuthorities().contains(new Role(Role.LIBRARIAN)))) {
        //         final var loggedReaderDetails = readerService.findByUsername(loggedUser.getUsername())
        //                 .orElseThrow(() -> new NotFoundException(ReaderDetails.class, loggedUser.getUsername()));

        //         //if logged Reader matches the one associated with the lending, skip ahead
        //         if (!loggedReaderDetails.getReaderNumber().equals(year + "/" + seq)) {
        //             throw new AccessDeniedException("Reader does not have permission to view another reader's photo");
        //         }
        //     }


        //     ReaderDetails readerDetails = readerService.findByReaderNumber(year + "/" + seq).orElseThrow(() -> new NotFoundException(ReaderDetails.class, loggedUser.getUsername()));

        //     //In case the user has no photo, just return a 200 OK without body
        //     if(readerDetails.getPhoto() == null) {
        //         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        //     }

        //     String photoFile = readerDetails.getPhoto().getPhotoFile();
        //     byte[] image = this.fileStorageService.getFile(photoFile);
        //     String fileFormat = this.fileStorageService.getExtension(readerDetails.getPhoto().getPhotoFile()).orElseThrow(() -> new ValidationException("Unable to get file extension"));

        //     if(image == null) {
        //         return ResponseEntity.ok().build();
        //     }

        //     return ResponseEntity.ok().contentType(fileFormat.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG).body(image);
        //
        return null;
    }

    @Operation(summary= "Gets a reader photo")
    @GetMapping("/photo")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getReaderOwnPhoto(Authentication authentication) {

        // TODO> Como fazer isto da autenticacao?
        // User loggedUser = userService.getAuthenticatedUser(authentication);

        // Optional<ReaderDetails> optReaderDetails = readerService.findByUsername(loggedUser.getUsername());
        // if(optReaderDetails.isEmpty()) {
        //     throw new AccessDeniedException("Could not find a valid reader from current auth");
        // }

        Optional<ReaderDetails> optReaderDetails = Optional.of(null);
        ReaderDetails readerDetails = optReaderDetails.get();

        //In case the user has no photo, just return a 200 OK without body
        if(readerDetails.getPhoto() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        byte[] image = this.fileStorageService.getFile(readerDetails.getPhoto().getPhotoFile());

        if(image == null) {
            return ResponseEntity.ok().build();
        }

        String fileFormat = this.fileStorageService.getExtension(readerDetails.getPhoto().getPhotoFile()).orElseThrow(() -> new ValidationException("Unable to get file extension"));

        return ResponseEntity.ok().contentType(fileFormat.equals("png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG).body(image);
    }

    @Operation(summary = "Gets the lendings of this reader by ISBN")
    @GetMapping(value = "/{year}/{seq}/lendings")
    public List<LendingView> getReaderLendings(
            Authentication authentication,
            @PathVariable("year")
            @Parameter(description = "The year of the Reader to find")
            final Integer year,
            @PathVariable("seq")
            @Parameter(description = "The sequencial of the Reader to find")
            final Integer seq,
            @RequestParam("isbn")
            @Parameter(description = "The ISBN of the Book to find")
            final String isbn,
            @RequestParam(value = "returned", required = false)
            @Parameter(description = "Filter by returned")
            final Optional<Boolean> returned)
    {
        String urlReaderNumber = year + "/" + seq;

        final var urlReaderDetails = readerService.findByReaderNumber(urlReaderNumber)
                .orElseThrow(() -> new NotFoundException(Lending.class, urlReaderNumber));

        // TODO> Como fazer isto da autenticacao?
        // User loggedUser = userService.getAuthenticatedUser(authentication);

        // //if Librarian is logged in, skip ahead
        // if (!(loggedUser.getAuthorities().contains(new Role(Role.LIBRARIAN)))) {
        //     final var loggedReaderDetails = readerService.findByUsername(loggedUser.getUsername())
        //             .orElseThrow(() -> new NotFoundException(ReaderDetails.class, loggedUser.getUsername()));

        //     //if logged Reader matches the one associated with the lendings, skip ahead
        //     if(!Objects.equals(loggedReaderDetails.getReaderNumber(), urlReaderDetails.getReaderNumber())){
        //         throw new AccessDeniedException("Reader does not have permission to view these lendings");
        //     }
        // }

        // TODO: ADD Lending
        // final var lendings = lendingService.listByReaderNumberAndIsbn(urlReaderNumber, isbn, returned);

        // if(lendings.isEmpty())
        //     throw new NotFoundException("No lendings found with provided ISBN");

        // return lendingViewMapper.toLendingView(lendings);

        return List.of(null);
    }

    @GetMapping("/top5")
    public ListResponse<ReaderView> getTop() {
        return new ListResponse<>(readerViewMapper.toReaderView(readerService.findTopReaders(5)));
    }

    @GetMapping("/top5ByGenre")
    public ListResponse<ReaderCountView> getTop5ReaderByGenre(
            @RequestParam("genre") String genre,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate)
    {
        final var books = readerService.findTopByGenre(genre,startDate,endDate);

        if(books.isEmpty())
            throw new NotFoundException("No lendings found with provided parameters");

        return new ListResponse<>(readerViewMapper.toReaderCountViewList(books));
    }
}
