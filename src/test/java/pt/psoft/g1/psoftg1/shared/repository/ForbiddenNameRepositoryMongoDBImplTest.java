package pt.psoft.g1.psoftg1.shared.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.ForbiddenNameMapperMongoDB;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.ForbiddenNameRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb.SpringDataForbiddenNameRepositoryMongoDB;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.mongodb.ForbiddenNameMongoDB;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForbiddenNameRepositoryMongoDBImplTest {
    @InjectMocks
    private ForbiddenNameRepositoryMongoDBImpl forbiddenRepo;

    @Mock
    private SpringDataForbiddenNameRepositoryMongoDB forbiddenNameRepository;

    @Mock
    private ForbiddenNameMapperMongoDB forbiddenNameEntityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll()
    {
        // Arrange
        ForbiddenNameMongoDB entity = mock(ForbiddenNameMongoDB.class);
        ForbiddenName model = mock(ForbiddenName.class);

        when(forbiddenNameRepository.findAll()).thenReturn(List.of(entity));
        when(forbiddenNameEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        Iterable<ForbiddenName> result = forbiddenRepo.findAll();

        // Assert
        assertTrue(result.iterator().hasNext());
        assertEquals(model, result.iterator().next());
    }

    @Test
    void testFindByForbiddenNameIsContained()
    {
        // Arrange
        ForbiddenNameMongoDB entity = mock(ForbiddenNameMongoDB.class);
        ForbiddenName model = mock(ForbiddenName.class);

        when(forbiddenNameRepository.findByForbiddenNameIsContained("bad")).thenReturn(List.of(entity));
        when(forbiddenNameEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        List<ForbiddenName> result = forbiddenRepo.findByForbiddenNameIsContained("bad");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.contains(model));
    }

    @Test
    void testSave()
    {
        // Arrange
        ForbiddenName model = mock(ForbiddenName.class);
        ForbiddenNameMongoDB entity = mock(ForbiddenNameMongoDB.class);
        ForbiddenName savedModel = mock(ForbiddenName.class);

        when(forbiddenNameEntityMapper.toMongoDB(model)).thenReturn(entity);
        when(forbiddenNameRepository.save(entity)).thenReturn(entity);
        when(forbiddenNameEntityMapper.toModel(entity)).thenReturn(savedModel);

        // Act
        ForbiddenName result = forbiddenRepo.save(model);

        // Assert
        assertEquals(savedModel, result);
    }

    @Test
    void testFindByForbiddenName()
    {
        // Arrange
        ForbiddenNameMongoDB entity = mock(ForbiddenNameMongoDB.class);
        ForbiddenName model = mock(ForbiddenName.class);

        when(forbiddenNameRepository.findByForbiddenName("badword")).thenReturn(Optional.of(entity));
        when(forbiddenNameEntityMapper.toModel(entity)).thenReturn(model);

        // Act
        Optional<ForbiddenName> result = forbiddenRepo.findByForbiddenName("badword");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(model, result.get());
    }

    @Test
    void testDeleteForbiddenName()
    {
        // Arrange
        when(forbiddenNameRepository.deleteForbiddenName("badword")).thenReturn(1);

        // Act
        int result = forbiddenRepo.deleteForbiddenName("badword");

        // Assert
        assertEquals(1, result);
    }
}
