package pt.psoft.g1.psoftg1.lendingmanagement.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb.BookRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingMapperMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.LendingRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.SpringDataLendingRepositoryMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb.ReaderDetailsRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoDB;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LendingRepositoryMongoDBImplTest {
    @InjectMocks
    private LendingRepositoryMongoDBImpl lendingRepo;

    @Mock
    private BookRepositoryMongoDBImpl bookRepo;

    @Mock
    private ReaderDetailsRepositoryMongoDBImpl readerDetailsRepo;

    @Mock
    private SpringDataLendingRepositoryMongoDB mongoRepo;

    @Mock
    private LendingMapperMongoDB lendingEntityMapper;

    @Mock
    private EntityManager em;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(lendingRepo, "mongoTemplate", mongoTemplate);
    }

    // Save
    @Test
    void testSaveLending() {
        // Arrange
        Lending lending = mock(Lending.class);
        LendingMongoDB lendingEntity = mock(LendingMongoDB.class);
        Book book = mock(Book.class);
        Isbn isbn = mock(Isbn.class);
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        BookMongoDB bookEntity = mock(BookMongoDB.class);
        ReaderDetailsMongoDB readerDetailsEntity = mock(ReaderDetailsMongoDB.class);
        Lending savedLending = mock(Lending.class);

        when(lending.getBook()).thenReturn(book);
        when(book.getIsbn()).thenReturn(isbn);
        when(isbn.getIsbn()).thenReturn("ABC123");
        when(bookRepo.findByIsbn("ABC123")).thenReturn(Optional.of(book));
        when(book.getBookId()).thenReturn("1L");

        when(lending.getReaderDetails()).thenReturn(readerDetails);
        when(readerDetails.getReaderNumber()).thenReturn("123");
        when(readerDetailsRepo.findByReaderNumber("123")).thenReturn(Optional.of(readerDetails));
        when(readerDetails.getReaderDetailsId()).thenReturn("2L");

        when(lendingEntityMapper.toEntity(lending)).thenReturn(lendingEntity);
        when(mongoRepo.save(lendingEntity)).thenReturn(lendingEntity);
        when(lendingEntityMapper.toModel(lendingEntity)).thenReturn(savedLending);

        // Act
        Lending result = lendingRepo.save(lending);

        // Assert
        assertEquals(savedLending, result);
    }

    // ------------------- GETTERS ------------------
    @Test
    void testFindByLendingNumber()
    {
        // Arrange
        Lending mockLending = mock(Lending.class);

        LendingMongoDB mockLendingEntity = mock(LendingMongoDB.class);

        when(mongoRepo.findByLendingNumber(anyString())).thenReturn(Optional.of(mockLendingEntity));
        when(lendingEntityMapper.toModel(mockLendingEntity)).thenReturn(mockLending);

        // Act
        Optional<Lending> lending = lendingRepo.findByLendingNumber(anyString());

        // Assert
        assertNotNull(lending);
        assertEquals(mockLending, lending.get());
    }

    @Test
    void testFindByLendingNumberEmpty()
    {
        // Arrange
        when(mongoRepo.findByLendingNumber(anyString())).thenReturn(Optional.empty());

        // Act
        Optional<Lending> lending = lendingRepo.findByLendingNumber(anyString());

        // Assert
        assertEquals(Optional.empty(), lending);
    }

    @Test
    void testListByReaderNumberAndIsbn()
    {
        // Arrange
        Lending mockLending = mock(Lending.class);

        List<LendingMongoDB> list = new ArrayList<>();
        LendingMongoDB mockLendingEntity = mock(LendingMongoDB.class);
        list.add(mockLendingEntity);

        when(mongoRepo.listByReaderNumberAndIsbn(anyString(), anyString())).thenReturn(list);
        when(lendingEntityMapper.toModel(mockLendingEntity)).thenReturn(mockLending);

        // Act
        List<Lending> lendings = lendingRepo.listByReaderNumberAndIsbn(anyString(), anyString());

        // Assert
        assertEquals(list.size(), lendings.size());
        assertTrue(lendings.contains(mockLending));
    }

    @Test
    void testListByReaderNumberAndIsbnEmtpy()
    {
        // Arrange
        List<LendingMongoDB> list = new ArrayList<>();

        when(mongoRepo.listByReaderNumberAndIsbn(anyString(), anyString())).thenReturn(list);

        // Act
        List<Lending> lendings = lendingRepo.listByReaderNumberAndIsbn(anyString(), anyString());

        // Assert
        assertEquals(list.size(), lendings.size());
        assertTrue(lendings.isEmpty());
    }

    @Test
    void testGetCountFromCurrentYear()
    {
        // Arrange
        int year = 2025;
        when(mongoRepo.getCountFromCurrentYear()).thenReturn(year);

        // Act
        int returned = lendingRepo.getCountFromCurrentYear();

        // Assert
        assertEquals(returned, year);
    }

    @Test
    void testListOutstandingByReaderNumber()
    {
        // Arrange
        Lending mockLending = mock(Lending.class);

        List<LendingMongoDB> list = new ArrayList<>();
        LendingMongoDB mockLendingEntity = mock(LendingMongoDB.class);
        list.add(mockLendingEntity);

        when(mongoRepo.listOutstandingByReaderNumber(anyString())).thenReturn(list);
        when(lendingEntityMapper.toModel(mockLendingEntity)).thenReturn(mockLending);

        // Act
        List<Lending> lendings = lendingRepo.listOutstandingByReaderNumber(anyString());

        // Assert
        assertEquals(list.size(), lendings.size());
        assertTrue(lendings.contains(mockLending));
    }

    @Test
    void testListOutstandingByReaderNumberEmpty()
    {
        // Arrange
        List<LendingMongoDB> list = new ArrayList<>();

        when(mongoRepo.listOutstandingByReaderNumber(anyString())).thenReturn(list);

        // Act
        List<Lending> lendings = lendingRepo.listOutstandingByReaderNumber(anyString());

        // Assert
        assertEquals(list.size(), lendings.size());
        assertTrue(lendings.isEmpty());
    }

    @Test
    void testGetOverdue() {
        // Arrange
        Page page = new Page(1, 10);
        LendingMongoDB lendingEntity = mock(LendingMongoDB.class);
        Lending lending = mock(Lending.class);

        List<LendingMongoDB> mockResults = List.of(lendingEntity);

        // Mock MongoTemplate to return the fake entity list
        when(mongoTemplate.find(any(Query.class), eq(LendingMongoDB.class)))
                .thenReturn(mockResults);

        // Mock mapper
        when(lendingEntityMapper.toModel(lendingEntity)).thenReturn(lending);


        // Act
        List<Lending> result = lendingRepo.getOverdue(page);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(lending));
    }

    @Test
    void testSearchLendings() {
        // Arrange
        Page page = mock(Page.class);
        String readerNumber = "123";
        String isbn = "ABC123";
        Boolean returned = true;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        LendingMongoDB lendingEntity = mock(LendingMongoDB.class);
        Lending lending = mock(Lending.class);

        List<LendingMongoDB> mockResults = List.of(lendingEntity);

        // Mock mongoTemplate.find() to return the fake entity list
        when(mongoTemplate.find(any(Query.class), eq(LendingMongoDB.class)))
                .thenReturn(mockResults);

        // Mock mapper
        when(lendingEntityMapper.toModel(lendingEntity)).thenReturn(lending);

        // Act
        List<Lending> result = lendingRepo.searchLendings(
                page, readerNumber, isbn, returned, startDate, endDate
        );

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(lending));
    }
}
