//package pt.psoft.g1.psoftg1.bookmanagement.model;
//
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "book_rating")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class BookRating {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "book_id", nullable = false)
//    private Book book;
//
//    @Column(nullable = false)
//    private String readerNumber;  // Número do leitor que fez a avaliação
//
//    @Column(nullable = false)
//    private Integer rating;       // 0-10
//
//    @Column(columnDefinition = "TEXT")
//    private String comment;       // Comentário do leitor
//
//    @Column(nullable = false)
//    private LocalDateTime createdAt;
//
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//}
//
