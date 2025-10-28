package pt.psoft.g1.psoftg1.lendingmanagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational.BookRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.relational.LendingRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.relational.SpringDataLendingRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational.ReaderDetailsRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.shared.services.Page;

/* Teste Unitario, opaque-box do LendingRepositoryImpl */
class LendingRepositoryRelationalImplTest
{
    @InjectMocks
    private LendingRepositoryRelationalImpl lendingRepo;

    @Mock
    private BookRepositoryRelationalImpl bookRepo;

    @Mock
    private ReaderDetailsRepositoryRelationalImpl readerDetailsRepo;
    
    @Mock
    private SpringDataLendingRepository sqlRepo;

    @Mock
    private LendingEntityMapper lendingEntityMapper;

    @Mock
    private EntityManager em;

    @BeforeEach
    void setUp() 
    {
        MockitoAnnotations.openMocks(this);
    }

    // Save
    @Test
    void testSaveLending() {
        // Arrange
        Lending lending = mock(Lending.class);
        LendingEntity lendingEntity = mock(LendingEntity.class);
        Book book = mock(Book.class);
        Isbn isbn = mock(Isbn.class);
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        BookEntity bookEntity = mock(BookEntity.class);
        ReaderDetailsEntity readerDetailsEntity = mock(ReaderDetailsEntity.class);
        Lending savedLending = mock(Lending.class);

        when(lending.getBook()).thenReturn(book);
        when(book.getIsbn()).thenReturn(isbn);
        when(isbn.getIsbn()).thenReturn("ABC123");
        when(bookRepo.findByIsbn("ABC123")).thenReturn(Optional.of(book));
        when(book.getBookId()).thenReturn("1L");
        when(em.getReference(BookEntity.class, "1L")).thenReturn(bookEntity);

        when(lending.getReaderDetails()).thenReturn(readerDetails);
        when(readerDetails.getReaderNumber()).thenReturn("123");
        when(readerDetailsRepo.findByReaderNumber("123")).thenReturn(Optional.of(readerDetails));
        when(readerDetails.getReaderDetailsId()).thenReturn("2L");
        when(em.getReference(ReaderDetailsEntity.class, "2L")).thenReturn(readerDetailsEntity);

        when(lendingEntityMapper.toEntity(lending)).thenReturn(lendingEntity);
        when(sqlRepo.save(lendingEntity)).thenReturn(lendingEntity);
        when(lendingEntityMapper.toModel(lendingEntity)).thenReturn(savedLending);

        // Act
        Lending result = lendingRepo.save(lending);

        // Assert
        assertEquals(savedLending, result);
    }

    // DELETERS
    // @Test
    // void testDeleteLending() {
    //     // Arrange
    //     Lending lending = mock(Lending.class);
    //     LendingEntity lendingEntity = mock(LendingEntity.class);

    //     when(lendingEntityMapper.toEntity(lending)).thenReturn(lendingEntity);
    //     doNothing().when(sqlRepo).delete(lendingEntity);

    //     // Act
    //     lendingRepo.delete(lending);

    //     // Assert
    //     // Sem asserts, apenas garantir que não lança exceções
    // }

    // ------------------- GETTERS ------------------
    @Test
    void testFindByLendingNumber()
    {
        // Arrange
        Lending mockLending = mock(Lending.class);

        LendingEntity mockLendingEntity = mock(LendingEntity.class);

        when(sqlRepo.findByLendingNumber(anyString())).thenReturn(Optional.of(mockLendingEntity));
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
        when(sqlRepo.findByLendingNumber(anyString())).thenReturn(Optional.empty());

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

        List<LendingEntity> list = new ArrayList<>();
        LendingEntity mockLendingEntity = mock(LendingEntity.class);
        list.add(mockLendingEntity);

        when(sqlRepo.listByReaderNumberAndIsbn(anyString(), anyString())).thenReturn(list);
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
        List<LendingEntity> list = new ArrayList<>();

        when(sqlRepo.listByReaderNumberAndIsbn(anyString(), anyString())).thenReturn(list);

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
        when(sqlRepo.getCountFromCurrentYear()).thenReturn(year);

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

        List<LendingEntity> list = new ArrayList<>();
        LendingEntity mockLendingEntity = mock(LendingEntity.class);
        list.add(mockLendingEntity);

        when(sqlRepo.listOutstandingByReaderNumber(anyString())).thenReturn(list);
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
        List<LendingEntity> list = new ArrayList<>();

        when(sqlRepo.listOutstandingByReaderNumber(anyString())).thenReturn(list);

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
        LendingEntity lendingEntity = mock(LendingEntity.class);
        Lending lending = mock(Lending.class);

        @SuppressWarnings("unchecked")
        CriteriaQuery<LendingEntity> cq = (CriteriaQuery<LendingEntity>) mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Root<LendingEntity> root = (Root<LendingEntity>) mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        @SuppressWarnings("unchecked")
        TypedQuery<LendingEntity> query = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(LendingEntity.class)).thenReturn(cq);
        when(cq.from(LendingEntity.class)).thenReturn(root);
        when(cb.isNull(root.get("returnedDate"))).thenReturn(p1);
        when(cb.lessThan(root.get("limitDate"), LocalDate.now())).thenReturn(p2);
        when(cq.where(p1, p2)).thenReturn(cq);
        when(cq.select(root)).thenReturn(cq);
        when(cq.orderBy(cb.asc(root.get("limitDate")))).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(lendingEntity));
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

        LendingEntity lendingEntity = mock(LendingEntity.class);
        Lending lending = mock(Lending.class);

        @SuppressWarnings("unchecked")
        CriteriaQuery<LendingEntity> cq = (CriteriaQuery<LendingEntity>) mock(CriteriaQuery.class);

        @SuppressWarnings("unchecked")
        Root<LendingEntity> root = (Root<LendingEntity>) mock(Root.class);

        @SuppressWarnings("unchecked")
        Join<Object, Object> bookJoin = mock(Join.class);

        @SuppressWarnings("unchecked")
        Join<Object, Object> readerJoin = mock(Join.class);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        Predicate p3 = mock(Predicate.class);
        Predicate p4 = mock(Predicate.class);
        Predicate p5 = mock(Predicate.class);

        @SuppressWarnings("unchecked")
        TypedQuery<LendingEntity> query = (TypedQuery<LendingEntity>) mock(TypedQuery.class);

        // Mock encadeamento de paths para readerNumber
        @SuppressWarnings("unchecked")
        Path<Object> readerNumberPath = (Path<Object>) mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<String> readerNumberFinalPath = (Path<String>) mock(Path.class);
        when(readerJoin.get("readerNumber")).thenReturn(readerNumberPath);
        when(readerNumberPath.get("readerNumber")).thenReturn((Path) readerNumberFinalPath);
        when(cb.like(readerNumberFinalPath, readerNumber)).thenReturn(p1);

        // Mock encadeamento de paths para isbn
        @SuppressWarnings("unchecked")
        Path<Object> isbnPath = (Path<Object>) mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<String> isbnFinalPath = (Path<String>) mock(Path.class);
        when(bookJoin.get("isbn")).thenReturn(isbnPath);
        when(isbnPath.get("isbn")).thenReturn((Path) isbnFinalPath);
        when(cb.like(isbnFinalPath, isbn)).thenReturn(p2);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(LendingEntity.class)).thenReturn(cq);
        when(cq.from(LendingEntity.class)).thenReturn(root);
        when(root.join("book")).thenReturn(bookJoin);
        when(root.join("readerDetails")).thenReturn(readerJoin);
        when(cb.isNotNull(root.get("returnedDate"))).thenReturn(p3);
        when(cb.greaterThanOrEqualTo(root.get("startDate"), startDate)).thenReturn(p4);
        when(cb.lessThanOrEqualTo(root.get("startDate"), endDate)).thenReturn(p5);

        when(cq.where(p1, p2, p3, p4, p5)).thenReturn(cq);
        when(cq.select(root)).thenReturn(cq);
        when(cq.orderBy(cb.asc(root.get("lendingNumber")))).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(query);
        when(query.setFirstResult(0)).thenReturn(query);
        when(query.setMaxResults(10)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(lendingEntity));
        when(lendingEntityMapper.toModel(lendingEntity)).thenReturn(lending);

        // Act
        List<Lending> result = lendingRepo.searchLendings(page, readerNumber, isbn, returned, startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(lending));
    }
}
