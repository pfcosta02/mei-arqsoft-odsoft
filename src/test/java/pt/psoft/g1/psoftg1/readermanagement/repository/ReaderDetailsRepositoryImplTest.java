package pt.psoft.g1.psoftg1.readermanagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational.ReaderDetailsRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational.SpringDataReaderRepositoryImpl;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational.UserRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;

class ReaderDetailsRepositoryImplTest {

    @InjectMocks
    private ReaderDetailsRepositoryRelationalImpl readerRepo;

    @Mock
    private SpringDataReaderRepositoryImpl sqlRepo;

    @Mock
    private UserRepositoryRelationalImpl userRepo;

    @Mock
    private ReaderDetailsEntityMapper readerEntityMapper;

    @Mock
    private EntityManager entityManager;

    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // SAVE
    @Test
    void testSaveReaderDetails() 
    {
        // Arrange
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails savedModel = mock(ReaderDetails.class);
        User user = mock(User.class);
        Reader reader = mock(Reader.class);
        ReaderEntity readerEntity = mock(ReaderEntity.class);

        when(readerDetails.getReader()).thenReturn(reader);
        when(reader.getUsername()).thenReturn("user123");
        when(userRepo.findByUsername("user123")).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn("1L");
        when(entityManager.getReference(ReaderEntity.class, "1L")).thenReturn(readerEntity);

        when(readerEntityMapper.toEntity(readerDetails)).thenReturn(entity);
        when(sqlRepo.save(entity)).thenReturn(entity);
        when(readerEntityMapper.toModel(entity)).thenReturn(savedModel);

        // Act
        ReaderDetails result = readerRepo.save(readerDetails);

        // Assert
        assertEquals(savedModel, result);
    }

    // GETTERS
    @Test
    void testFindByReaderNumber() 
    {
        // Arrange
        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(sqlRepo.findByReaderNumber(anyString())).thenReturn(Optional.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        Optional<ReaderDetails> result = readerRepo.findByReaderNumber(anyString());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(model, result.get());
    }

    @Test
    void testFindByPhoneNumber() 
    {
        // Arrange
        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(sqlRepo.findByPhoneNumber("912345678")).thenReturn(List.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        List<ReaderDetails> result = readerRepo.findByPhoneNumber("912345678");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(model));
    }

    @Test
    void testFindByUsername() 
    {
        // Arrange
        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(sqlRepo.findByUsername("user@example.com")).thenReturn(Optional.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        Optional<ReaderDetails> result = readerRepo.findByUsername("user@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(model, result.get());
    }

    @Test
    void testFindByUserId() 
    {
        // Arrange
        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(sqlRepo.findByUserId("user-id")).thenReturn(Optional.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        Optional<ReaderDetails> result = readerRepo.findByUserId("user-id");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(model, result.get());
    }

    @Test
    void testGetCountFromCurrentYear() 
    {
        when(readerRepo.getCountFromCurrentYear()).thenReturn(42);

        int result = readerRepo.getCountFromCurrentYear();

        assertEquals(42, result);
    }

    @Test
    void testFindAllReaderDetails() 
    {
        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(sqlRepo.findAll()).thenReturn(List.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        Iterable<ReaderDetails> result = readerRepo.findAll();

        assertTrue(result.iterator().hasNext());
        assertEquals(model, result.iterator().next());
    }

    @Test
    void testFindTopReaders() 
    {
        // Arrange
        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails model = mock(ReaderDetails.class);
        Pageable pageable = PageRequest.of(0, 5);
        List<ReaderDetailsEntity> entityList = List.of(entity);
        List<ReaderDetails> modelList = List.of(model);

        when(sqlRepo.findTopReaders(pageable)).thenReturn(entityList);
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        List<ReaderDetails> result = readerRepo.findTopReaders(pageable);

        // Assert
        assertEquals(modelList, result);
    }

    @Test
    void testFindTopByGenre() 
    {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        ReaderBookCountDTO mockDTO = mock(ReaderBookCountDTO.class);
        List<ReaderBookCountDTO> dtoList = List.of(mockDTO);

        when(sqlRepo.findTopByGenre(
                pageable, "Fantasy",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31))
        ).thenReturn(dtoList);
        
        // Act
        List<ReaderBookCountDTO> result = readerRepo.findTopByGenre(
                pageable, "Fantasy",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31)
        );

        // Assert
        assertEquals(1, result.size());
        assertEquals(mockDTO, result.get(0));    }

    // @Test
    // void testDeleteReaderDetails() 
    // {
    //     // Arrange
    //     ReaderDetails readerDetails = mock(ReaderDetails.class);
    //     ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);

    //     when(readerEntityMapper.toEntity(readerDetails)).thenReturn(entity);
    //     doNothing().when(sqlRepo).delete(entity);

    //     // Act
    //     readerRepo.delete(readerDetails);
    // }

    @Test
    void testSearchReaderDetailsWithAllFields() 
    {
        // Arrange
        pt.psoft.g1.psoftg1.shared.services.Page page = mock(pt.psoft.g1.psoftg1.shared.services.Page.class);
        SearchReadersQuery query = mock(SearchReadersQuery.class);

        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);
        when(query.getName()).thenReturn("Ana");
        when(query.getEmail()).thenReturn("ana@example.com");
        when(query.getPhoneNumber()).thenReturn("912345678");

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<ReaderDetailsEntity> cq = mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Root<ReaderDetailsEntity> root = mock(Root.class);
        @SuppressWarnings("unchecked")
        Join<Object, Object> userJoin = mock(Join.class);
        @SuppressWarnings("unchecked")
        TypedQuery<ReaderDetailsEntity> typedQuery = mock(TypedQuery.class);

        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        Predicate p3 = mock(Predicate.class);

        @SuppressWarnings("unchecked")
        Path<Object> namePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<String> nameFinalPath = mock(Path.class);
        when(userJoin.get("name")).thenReturn(namePath);
        when(namePath.get("name")).thenReturn((Path) nameFinalPath);
        when(cb.like(nameFinalPath, "%Ana%")).thenReturn(p1);

        @SuppressWarnings("unchecked")
        Path<String> emailPath = mock(Path.class);
        when(userJoin.get("username")).thenReturn((Path)emailPath);
        when(cb.equal(emailPath, "ana@example.com")).thenReturn(p2);

        @SuppressWarnings("unchecked")
        Path<Object> phonePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        Path<String> phoneFinalPath = mock(Path.class);
        when(root.get("phoneNumber")).thenReturn(phonePath);
        when(phonePath.get("phoneNumber")).thenReturn((Path) phoneFinalPath);
        when(cb.equal(phoneFinalPath, "912345678")).thenReturn(p3);

        ReaderDetailsEntity entity = mock(ReaderDetailsEntity.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(ReaderDetailsEntity.class)).thenReturn(cq);
        when(cq.from(ReaderDetailsEntity.class)).thenReturn(root);
        when(root.join("reader")).thenReturn(userJoin);
        when(cq.select(root)).thenReturn(cq);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cq.orderBy(anyList())).thenReturn(cq);
        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.setFirstResult(0)).thenReturn(typedQuery);
        when(typedQuery.setMaxResults(10)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        List<ReaderDetails> result = readerRepo.searchReaderDetails(page, query);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(model));
    }

}
