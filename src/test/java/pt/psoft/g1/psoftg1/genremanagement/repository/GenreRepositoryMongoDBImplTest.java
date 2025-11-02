package pt.psoft.g1.psoftg1.genremanagement.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreMapperMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.GenreRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb.SpringDataGenreRepositoryMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.redis.GenreRepositoryRedisImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class GenreRepositoryMongoDBImplTest {
    @InjectMocks
    private GenreRepositoryMongoDBImpl genreRepo;

    @Mock
    private GenreRepositoryRedisImpl redisRepo;

    @Mock
    private SpringDataGenreRepositoryMongoDB springDataGenreRepo;

    @Mock
    private GenreMapperMongoDB genreEntityMapper;

    @Mock
    private EntityManager entityManager;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(genreRepo, "mongoTemplate", mongoTemplate);
    }

    @Test
    void testFindAllGenres() {
        GenreMongoDB genreEntity = mock(GenreMongoDB.class);
        Genre genre = mock(Genre.class);

        List<GenreMongoDB> entities = List.of(genreEntity);
        when(springDataGenreRepo.findAll()).thenReturn(entities);
        when(genreEntityMapper.toModel(genreEntity)).thenReturn(genre);

        Iterable<Genre> result = genreRepo.findAll();

        assertTrue(result.iterator().hasNext());
        assertEquals(genre, result.iterator().next());
    }

    @Test
    void testFindAllGenresEmpty() {
        when(springDataGenreRepo.findAll()).thenReturn(Collections.emptyList());

        Iterable<Genre> result = genreRepo.findAll();

        assertTrue(!result.iterator().hasNext());
    }

    @Test
    void testFindByStringPresent() {
        GenreMongoDB genreEntity = mock(GenreMongoDB.class);
        Genre genre = mock(Genre.class);
        Optional<Genre> mockOptGenre = Optional.of(genre);

        when(redisRepo.getGenreFromRedis(anyString())).thenReturn(mockOptGenre);
        when(springDataGenreRepo.findByString("Fiction")).thenReturn(Optional.of(genreEntity));
        when(genreEntityMapper.toModel(genreEntity)).thenReturn(genre);

        Optional<Genre> result = genreRepo.findByString("Fiction");
        doNothing().when(redisRepo).save(genre);

        assertTrue(result.isPresent());
        assertEquals(genre, result.get());
    }

    @Test
    void testFindByStringEmpty() {
        when(redisRepo.getGenreFromRedis(anyString())).thenReturn(Optional.empty());
        when(springDataGenreRepo.findByString("Unknown")).thenReturn(Optional.empty());

        Optional<Genre> result = genreRepo.findByString("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveGenre() {
        Genre genre = mock(Genre.class);
        GenreMongoDB genreEntity = mock(GenreMongoDB.class);

        when(genreEntityMapper.toMongoDB(genre)).thenReturn(genreEntity);
        when(springDataGenreRepo.save(genreEntity)).thenReturn(genreEntity);
        when(genreEntityMapper.toModel(genreEntity)).thenReturn(genre);

        Genre savedGenre = genreRepo.save(genre);
        doNothing().when(redisRepo).save(genre);

        assertEquals(genre, savedGenre);
    }

    @Test
    void testFindTop5GenreByBookCount() {
        GenreBookCountDTO dto = mock(GenreBookCountDTO.class);
        Pageable pageable = PageRequest.of(0, 5);
        List<GenreBookCountDTO> list = List.of(dto);

        when(redisRepo.getGenreBookCountListFromRedis(anyString())).thenReturn(list);
        when(springDataGenreRepo.findTop5GenreByBookCount(pageable)).thenReturn(list);

        List<GenreBookCountDTO> result = genreRepo.findTop5GenreByBookCount(pageable);
        doNothing().when(redisRepo).cacheGenreBookCountListToRedis(anyString(), eq(result));

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void testGetLendingsAverageDurationPerMonth() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        GenreLendingsDTO rawDto = mock(GenreLendingsDTO.class);
        when(rawDto.getGenre()).thenReturn("Fiction");
        when(rawDto.getYear()).thenReturn(2024);
        when(rawDto.getMonth()).thenReturn(5);
        when(rawDto.getValue()).thenReturn(4.2);

        when(springDataGenreRepo.getLendingsAverageDurationPerMonth(startDate, endDate))
                .thenReturn(List.of(rawDto));
        // Act
        List<GenreLendingsPerMonthDTO> result = genreRepo.getLendingsAverageDurationPerMonth(startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        GenreLendingsPerMonthDTO dto = result.get(0);
        assertEquals(2024, dto.getYear());
        assertEquals(5, dto.getMonth());
        assertEquals("Fiction", dto.getValues().get(0).getGenre());
        assertEquals(4.2, dto.getValues().get(0).getValue());
    }


    @Test
    void testGetLendingsPerMonthLastYearByGenre() {
        LocalDate now = LocalDate.now();
        LocalDate twelveMonthsAgo = now.minusMonths(12);

        GenreLendingsDTO fantasyDto = new GenreLendingsDTO("Fantasy", 5L);
        fantasyDto.setYear(2025);
        fantasyDto.setMonth(10);

        List<GenreLendingsDTO> mockResults = List.of(fantasyDto);

        // Mock repository call
        when(springDataGenreRepo.getLendingsPerMonthByGenre(twelveMonthsAgo, now))
                .thenReturn(mockResults);

        // Act
        List<GenreLendingsPerMonthDTO> result = genreRepo.getLendingsPerMonthLastYearByGenre();

        // Assert
        assertEquals(1, result.size());
        GenreLendingsPerMonthDTO dto = result.get(0);
        assertEquals(2025, dto.getYear());
        assertEquals(10, dto.getMonth());
        assertEquals(1, dto.getValues().size());
        assertEquals("Fantasy", dto.getValues().get(0).getGenre());
    }
}
