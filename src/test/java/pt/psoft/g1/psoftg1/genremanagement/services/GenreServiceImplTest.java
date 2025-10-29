package pt.psoft.g1.psoftg1.genremanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GenreServiceImplTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreServiceImpl genreService;

    private Genre genre;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        genre = mock(Genre.class);
        when(genre.getGenre()).thenReturn("Fiction");

    }

    @Test
    void testFindByString() {
        when(genreRepository.findByString("Fiction")).thenReturn(Optional.of(genre));

        Optional<Genre> result = genreService.findByString("Fiction");

        assertTrue(result.isPresent());
        assertEquals(genre, result.get());
    }

    @Test
    void testFindAll() {
        when(genreRepository.findAll()).thenReturn(List.of(genre));

        Iterable<Genre> result = genreService.findAll();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext());
        assertEquals(genre, result.iterator().next());
    }

    @Test
    void testFindTopGenreByBooks() {
        GenreBookCountDTO mockDTO = mock(GenreBookCountDTO.class);
        when(mockDTO.getGenre()).thenReturn("Fiction");
        when(mockDTO.getBookCount()).thenReturn(10L);
        List<GenreBookCountDTO> mockList = List.of(mockDTO);

        when(genreRepository.findTop5GenreByBookCount(any())).thenReturn(mockList);

        List<GenreBookCountDTO> result = genreService.findTopGenreByBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Fiction", result.get(0).getGenre());
    }

    @Test
    void testSave() {
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        Genre result = genreService.save(genre);

        assertNotNull(result);
        assertEquals(genre, result);
    }

    @Test
    void testGetLendingsPerMonthLastYearByGenre() {
        when(genreRepository.getLendingsPerMonthLastYearByGenre()).thenReturn(Collections.emptyList());

        List<GenreLendingsPerMonthDTO> result = genreService.getLendingsPerMonthLastYearByGenre();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAverageLendings() {
        GetAverageLendingsQuery query = new GetAverageLendingsQuery(2023, 1);
        Page page = new Page(1, 10);
        when(genreRepository.getAverageLendingsInMonth(any(), any())).thenReturn(Collections.emptyList());

        List<GenreLendingsDTO> result = genreService.getAverageLendings(query, page);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetLendingsAverageDurationPerMonth_ValidDates() {
        String start = "2023-01-01";
        String end = "2023-12-31";
        when(genreRepository.getLendingsAverageDurationPerMonth(any(), any())).thenReturn(List.of(mock(GenreLendingsPerMonthDTO.class)));

        List<GenreLendingsPerMonthDTO> result = genreService.getLendingsAverageDurationPerMonth(start, end);

        assertNotNull(result);
        assertFalse(result.isEmpty());

    }

    @Test
    void testGetLendingsAverageDurationPerMonth_InvalidDateFormat() {
        String start = "invalid-date";
        String end = "2023-12-31";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            genreService.getLendingsAverageDurationPerMonth(start, end);
        });

        assertEquals("Expected format is YYYY-MM-DD", exception.getMessage());
    }

    @Test
    void testGetLendingsAverageDurationPerMonth_StartDateAfterEndDate() {
        String start = "2023-12-31";
        String end = "2023-01-01";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            genreService.getLendingsAverageDurationPerMonth(start, end);
        });

        assertEquals("Start date cannot be after end date", exception.getMessage());
    }

    @Test
    void testGetLendingsAverageDurationPerMonth_NoDataFound() {
        String start = "2023-01-01";
        String end = "2023-12-31";
        when(genreRepository.getLendingsAverageDurationPerMonth(any(), any())).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(NotFoundException.class, () -> {
            genreService.getLendingsAverageDurationPerMonth(start, end);
        });

        assertEquals("No objects match the provided criteria", exception.getMessage());
    }
}