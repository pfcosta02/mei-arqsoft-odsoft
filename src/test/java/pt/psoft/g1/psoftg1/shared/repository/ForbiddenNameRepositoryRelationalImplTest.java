package pt.psoft.g1.psoftg1.shared.repository;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational.ForbiddenNameRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational.SpringDataForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.ForbiddenNameEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.relational.ForbiddenNameEntity;

class ForbiddenNameRepositoryRelationalImplTest {

    @InjectMocks
    private ForbiddenNameRepositoryRelationalImpl forbiddenRepo;

    @Mock
    private SpringDataForbiddenNameRepository forbiddenNameRepository;

    @Mock
    private ForbiddenNameEntityMapper forbiddenNameEntityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() 
    {
        // Arrange
        ForbiddenNameEntity entity = mock(ForbiddenNameEntity.class);
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
        ForbiddenNameEntity entity = mock(ForbiddenNameEntity.class);
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
        ForbiddenNameEntity entity = mock(ForbiddenNameEntity.class);
        ForbiddenName savedModel = mock(ForbiddenName.class);

        when(forbiddenNameEntityMapper.toEntity(model)).thenReturn(entity);
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
        ForbiddenNameEntity entity = mock(ForbiddenNameEntity.class);
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
