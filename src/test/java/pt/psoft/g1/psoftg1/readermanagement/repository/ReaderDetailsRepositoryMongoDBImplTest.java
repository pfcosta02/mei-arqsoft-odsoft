package pt.psoft.g1.psoftg1.readermanagement.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.util.ReflectionTestUtils;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsMapperMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb.ReaderDetailsRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb.SpringDataReaderRepositoryMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.shared.model.Name;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserMapperMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserReaderMapper;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb.UserRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReaderDetailsRepositoryMongoDBImplTest {
    @InjectMocks
    private ReaderDetailsRepositoryMongoDBImpl readerRepo;

    @Mock
    private SpringDataReaderRepositoryMongoDB mongoRepo;

    @Mock
    private UserRepositoryMongoDBImpl userRepo;

    @Mock
    private UserMapperMongoDB userMapperMongoDB;

    @Mock
    private ReaderDetailsMapperMongoDB readerEntityMapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(readerRepo, "mongoTemplate", mongoTemplate);
    }

    @Test
    void testSaveReaderDetails()
    {
        // Arrange
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails savedModel = mock(ReaderDetails.class);
        User user = mock(User.class);
        Reader reader = mock(Reader.class);
        ReaderMongoDB readerMongoDB = mock(ReaderMongoDB.class);
        Name name = mock(Name.class);

        // Mock domain model to get Reader
        when(readerDetails.getReader()).thenReturn(reader);
        when(reader.getUsername()).thenReturn("user123");

        // Mock user repository
        when(userRepo.findByUsername("user123")).thenReturn(Optional.of(user));

        // Mock user properties for conversion
        when(user.getUsername()).thenReturn("user123");
        when(user.getPassword()).thenReturn("pass");
        when(name.toString()).thenReturn("User Name");
        when(user.getName()).thenReturn(name);
        when(user.isEnabled()).thenReturn(true);
        when(user.getAuthorities()).thenReturn(Set.of(new Role(Role.READER)));

        // Mock conversion mappers
        when(readerEntityMapper.toEntity(readerDetails)).thenReturn(entity);
        when(userMapperMongoDB.toEntity(any(Reader.class))).thenReturn(readerMongoDB);
        when(mongoRepo.save(entity)).thenReturn(entity);
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
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(mongoRepo.findByReaderNumber(anyString())).thenReturn(Optional.of(entity));
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
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(mongoRepo.findByPhoneNumber("912345678")).thenReturn(List.of(entity));
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
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(mongoRepo.findByUsername("user@example.com")).thenReturn(Optional.of(entity));
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
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(mongoRepo.findByUserId("user-id")).thenReturn(Optional.of(entity));
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
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(mongoRepo.findAll()).thenReturn(List.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        Iterable<ReaderDetails> result = readerRepo.findAll();

        assertTrue(result.iterator().hasNext());
        assertEquals(model, result.iterator().next());
    }

    @Test
    void testFindTopReaders()
    {
        // Arrange
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails model = mock(ReaderDetails.class);
        Pageable pageable = PageRequest.of(0, 5);
        List<ReaderDetailsMongoDB> entityList = List.of(entity);
        List<ReaderDetails> modelList = List.of(model);

        when(mongoRepo.findTopReaders(pageable)).thenReturn(entityList);
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

        when(mongoRepo.findTopByGenre(
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
        assertEquals(mockDTO, result.get(0));
    }


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

        // Mock MongoDB query result
        ReaderDetailsMongoDB entity = mock(ReaderDetailsMongoDB.class);
        ReaderDetails model = mock(ReaderDetails.class);

        when(mongoTemplate.find(any(Query.class), eq(ReaderDetailsMongoDB.class))).thenReturn(List.of(entity));
        when(readerEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        List<ReaderDetails> result = readerRepo.searchReaderDetails(page, query);

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(model));
    }
}
