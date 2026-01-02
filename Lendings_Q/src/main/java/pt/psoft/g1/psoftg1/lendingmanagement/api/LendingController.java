package pt.psoft.g1.psoftg1.lendingmanagement.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;

import java.util.List;

@RestController
@RequestMapping("/api/lendings")
@RequiredArgsConstructor
public class LendingController {

    private final LendingService service;

    @GetMapping
    public ResponseEntity<List<LendingQueryDTO>> getAllLendings() {
        return ResponseEntity.ok(service.getAllLendings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LendingQueryDTO> getLendingById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getLendingById(id));
    }

//    @GetMapping("/reader/{readerId}")
//    public ResponseEntity<List<LendingQueryDTO>> getLendingsByReader(@PathVariable Long readerId) {
//        return ResponseEntity.ok(service.getLendingsByReader(readerId));
//    }
}