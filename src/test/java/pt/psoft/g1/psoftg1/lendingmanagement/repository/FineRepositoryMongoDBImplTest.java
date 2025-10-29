package pt.psoft.g1.psoftg1.lendingmanagement.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.FineMapperMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.FineRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb.SpringDataFineRepositoryMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoDB;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FineRepositoryMongoDBImplTest {
    @InjectMocks
    private FineRepositoryMongoDBImpl fineRepo;

    @Mock
    private SpringDataFineRepositoryMongoDB springDataFineRepo;

    @Mock
    private FineMapperMongoDB fineEntityMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByLendingNumberPresent()
    {
        // arrange
        FineMongoDB fineEntity = mock(FineMongoDB.class);
        Fine fine = mock(Fine.class);

        when(springDataFineRepo.findByLendingNumber("LN123")).thenReturn(Optional.of(fineEntity));
        when(fineEntityMapper.toModel(fineEntity)).thenReturn(fine);

        // Act
        Optional<Fine> result = fineRepo.findByLendingNumber("LN123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(fine, result.get());
    }

    @Test
    void testFindByLendingNumberEmpty()
    {
        // Arrange
        when(springDataFineRepo.findByLendingNumber("LN999")).thenReturn(Optional.empty());

        // Act
        Optional<Fine> result = fineRepo.findByLendingNumber("LN999");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllFines()
    {
        // Arrange
        FineMongoDB fineEntity = mock(FineMongoDB.class);
        Fine fine = mock(Fine.class);

        when(springDataFineRepo.findAll()).thenReturn(List.of(fineEntity));
        when(fineEntityMapper.toModel(fineEntity)).thenReturn(fine);

        // Act
        Iterable<Fine> result = fineRepo.findAll();

        // Assert
        assertTrue(result.iterator().hasNext());
        assertEquals(fine, result.iterator().next());
    }

    @Test
    void testFindAllFinesEmpty()
    {
        // Arrange
        when(springDataFineRepo.findAll()).thenReturn(Collections.emptyList());

        // Act
        Iterable<Fine> result = fineRepo.findAll();

        // Assert
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void testSaveFine()
    {
        // Arrange
        Fine fine = mock(Fine.class);
        FineMongoDB fineEntity = mock(FineMongoDB.class);

        when(fineEntityMapper.toEntity(fine)).thenReturn(fineEntity);
        when(springDataFineRepo.save(fineEntity)).thenReturn(fineEntity);
        when(fineEntityMapper.toModel(fineEntity)).thenReturn(fine);

        // Act
        Fine savedFine = fineRepo.save(fine);

        // Assert
        assertEquals(fine, savedFine);
    }
}
