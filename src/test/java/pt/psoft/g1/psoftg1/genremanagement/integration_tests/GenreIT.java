package pt.psoft.g1.psoftg1.genremanagement.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Integration test opaque-box do Controller + Service + Domain */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GenreIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private LendingRepository lendingRepository;

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnAverageLendingsPerGenre() throws Exception {
        GenreLendingsDTO dto = mock(GenreLendingsDTO.class);
        when(dto.getGenre()).thenReturn("Fiction");
        when(dto.getValue()).thenReturn(10);

        when(genreRepository.getAverageLendingsInMonth(any(), any())).thenReturn(List.of(dto));

        String requestBody = """
            {
                "query": { "month": 1, "year": 2025 },
                "page": { "number": 1, "limit": 10 }
            }
            """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/genres/avgLendingsPerGenre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].genre").value("Fiction"))
                .andExpect(jsonPath("$.items[0].value").value(10));

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnTop5GenresByBookCount() throws Exception {
        GenreBookCountDTO dto = mock(GenreBookCountDTO.class);

        when(genreRepository.findTop5GenreByBookCount(any())).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/genres/top5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnLendingsPerMonthLastYearByGenre() throws Exception {
        GenreLendingsPerMonthDTO dto = mock(GenreLendingsPerMonthDTO.class);


        when(genreRepository.getLendingsPerMonthLastYearByGenre()).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/genres/lendingsPerMonthLastTwelveMonths"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnLendingsAverageDurationPerMonth() throws Exception {
        GenreLendingsPerMonthDTO dto = mock(GenreLendingsPerMonthDTO.class);

        when(genreRepository.getLendingsAverageDurationPerMonth(any(), any())).thenReturn(List.of(dto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/genres/lendingsAverageDurationPerMonth")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isOk());
    }
}