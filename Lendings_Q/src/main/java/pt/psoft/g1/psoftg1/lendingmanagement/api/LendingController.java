package pt.psoft.g1.psoftg1.lendingmanagement.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;

import java.util.List;

@RestController
@RequestMapping("/api/lendings")
@RequiredArgsConstructor
@Slf4j
public class LendingController {

        private final LendingService lendingQueryService;

        @GetMapping
        public ResponseEntity<List<LendingQueryDTO>> getAllLendings() {
            log.debug("GET all lendings");
            return ResponseEntity.ok(lendingQueryService.getAllLendings());
        }

        @GetMapping("/{year}/{seq}")
        public ResponseEntity<LendingQueryDTO> getLendingByNumber(
                @PathVariable int year,
                @PathVariable int seq) {

            String lendingNumber = year + "/" + seq;
            log.debug("GET lending: {}", lendingNumber);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/hal+json"))
                    .body(lendingQueryService.getLendingByNumber(lendingNumber));
        }

        @GetMapping("/reader/{readerNumber}")
        public ResponseEntity<List<LendingQueryDTO>> getLendingsByReader(@PathVariable String readerNumber) {
            log.debug("GET lendings by reader: {}", readerNumber);
            return ResponseEntity.ok(lendingQueryService.getLendingsByReader(readerNumber));
        }

        @GetMapping("/reader/{readerNumber}/active")
        public ResponseEntity<List<LendingQueryDTO>> getActiveLendingsByReader(@PathVariable String readerNumber) {
            log.debug("GET active lendings by reader: {}", readerNumber);
            return ResponseEntity.ok(lendingQueryService.getActiveLendingsByReader(readerNumber));
        }

        @GetMapping("/overdue")
        public ResponseEntity<List<LendingQueryDTO>> getOverdueLendings() {
            log.debug("GET overdue lendings");
            return ResponseEntity.ok(lendingQueryService.getOverdueLendings());
        }

        @GetMapping("/stats/count")
        public ResponseEntity<Integer> countCurrentYear() {
            log.debug("GET count current year");
            return ResponseEntity.ok(lendingQueryService.countLendingsCurrentYear());
        }

        @GetMapping("/stats/average-duration")
        public ResponseEntity<Double> getAverageDuration() {
            log.debug("GET average duration");
            return ResponseEntity.ok(lendingQueryService.getAverageLendingDuration());
        }
    }
