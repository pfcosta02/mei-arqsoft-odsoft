package pt.psoft.g1.psoftg1.genremanagement.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreService;
import pt.psoft.g1.psoftg1.genremanagement.services.GetAverageLendingsQuery;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenreService genreService;

    @MockBean
    private GenreViewMapper genreViewMapper;

    private GenreLendingsView genreLendingsView;
    private List<GenreLendingsView> genreLendingsViews;

    @BeforeEach
    void setUp() {
        genreLendingsView = mock(GenreLendingsView.class);
        when(genreLendingsView.getGenre()).thenReturn("Fiction");
        when(genreLendingsView.getValue()).thenReturn(10);

        genreLendingsViews = List.of(genreLendingsView);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAverageLendings() throws Exception 
    {
        // Arrange
        GenreLendingsDTO genreLendingsDTO = mock(GenreLendingsDTO.class);
        when(genreLendingsDTO.getGenre()).thenReturn("Fiction");
        when(genreLendingsDTO.getValue()).thenReturn(10);

        List<GenreLendingsDTO> genreLendingsDTOs = List.of(genreLendingsDTO);
        when(genreService.getAverageLendings(any(GetAverageLendingsQuery.class), any()))
                .thenReturn(genreLendingsDTOs);
        when(genreViewMapper.toGenreAvgLendingsView(anyList())).thenReturn(genreLendingsViews);

        // Act + Assert
        mockMvc.perform(post("/api/genres/avgLendingsPerGenre")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\": {\"month\": 1, \"year\": 2025}, \"page\": {}}")).andDo(result -> {
            System.out.println(result.getResponse().getContentAsString());
        })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].genre").value("Fiction"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTopGenres() throws Exception {
        // Arrange
        GenreBookCountView genreBookCountView = mock(GenreBookCountView.class);
        GenreView genreView = mock(GenreView.class);
        when(genreBookCountView.getGenreView()).thenReturn(genreView);
        when(genreBookCountView.getGenreView().getGenre()).thenReturn("Fiction");
        when(genreBookCountView.getBookCount()).thenReturn(10L);

        GenreBookCountDTO genreBookCountDTO = mock(GenreBookCountDTO.class);
        when(genreBookCountDTO.getGenre()).thenReturn("Fiction");
        when(genreBookCountDTO.getBookCount()).thenReturn(10L);

        List<GenreBookCountDTO> genreBookCountDTOs = List.of(genreBookCountDTO);

        List<GenreBookCountView> genreBookCountViews = List.of(genreBookCountView);

        when(genreService.findTopGenreByBooks()).thenReturn(genreBookCountDTOs);
        when(genreViewMapper.toGenreBookCountView(anyList())).thenReturn(genreBookCountViews);

        // Act + Assert
        mockMvc.perform(get("/api/genres/top5")
                .with(SecurityMockMvcRequestPostProcessors.csrf())).andDo(result -> { System.out.println(result.getResponse().getContentAsString()); })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].genreView.genre").value("Fiction"))
                .andExpect(jsonPath("$.items[0].bookCount").value(10));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLendingsPerMonthLastYearNoGenres() throws Exception 
    {
        // Arrange
        when(genreService.getLendingsPerMonthLastYearByGenre()).thenReturn(Collections.emptyList());

        // Act + Assert
        mockMvc.perform(get("/api/genres/lendingsPerMonthLastTwelveMonths")
                .with(SecurityMockMvcRequestPostProcessors.csrf())).andDo(result -> { System.out.println(result.getResponse().getContentAsString()); })
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details[0]").value("No genres to show"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLendingsPerMonthLastYearByGenres() throws Exception 
    {
        // Arrange
        GenreLendingsView genreLendingsView = mock(GenreLendingsView.class);
        when(genreLendingsView.getGenre()).thenReturn("Fiction");
        when(genreLendingsView.getValue()).thenReturn(5.0);

        GenreLendingsCountPerMonthView view = mock(GenreLendingsCountPerMonthView.class);
        when(view.getMonth()).thenReturn(1);
        when(view.getLendingsCount()).thenReturn(List.of(genreLendingsView));

        GenreLendingsDTO dto = mock(GenreLendingsDTO.class);
        when(dto.getGenre()).thenReturn("Fiction");
        when(dto.getValue()).thenReturn(5.0);

        GenreLendingsPerMonthDTO dtos = mock(GenreLendingsPerMonthDTO.class);
        when(dtos.getMonth()).thenReturn(1);
        when(dtos.getValues()).thenReturn(List.of(dto));

        List<GenreLendingsCountPerMonthView> views = List.of(view);
        List<GenreLendingsPerMonthDTO> listDTOs = List.of(dtos);

        when(genreService.getLendingsPerMonthLastYearByGenre()).thenReturn(listDTOs);
        when(genreViewMapper.toGenreLendingsCountPerMonthView(anyList())).thenReturn(views);

        // Act + Assert
        mockMvc.perform(get("/api/genres/lendingsPerMonthLastTwelveMonths")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())).andDo(result -> { System.out.println(result.getResponse().getContentAsString()); })
                .andExpect(status().isOk()).andDo(result -> { System.out.println(result.getResponse().getContentAsString()); })
                .andExpect(jsonPath("$.items[0].lendingsCount[0].genre").value("Fiction"))
                .andExpect(jsonPath("$.items[0].lendingsCount[0].value").value(5.0));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLendingsAverageDurationPerMonth() throws Exception 
    {
        // Arrange
        GenreLendingsView genreLendingsView = mock(GenreLendingsView.class);
        when(genreLendingsView.getGenre()).thenReturn("Fiction");
        when(genreLendingsView.getValue()).thenReturn(5.0);

        GenreLendingsAvgPerMonthView view = mock(GenreLendingsAvgPerMonthView.class);
        when(view.getMonth()).thenReturn(1);
        when(view.getDurationAverages()).thenReturn(List.of(genreLendingsView));

        GenreLendingsDTO dto = mock(GenreLendingsDTO.class);
        when(dto.getGenre()).thenReturn("Fiction");
        when(dto.getValue()).thenReturn(5.0);

        GenreLendingsPerMonthDTO dtos = mock(GenreLendingsPerMonthDTO.class);
        when(dtos.getMonth()).thenReturn(1);
        when(dtos.getValues()).thenReturn(List.of(dto));

        List<GenreLendingsAvgPerMonthView> views = List.of(view);
        List<GenreLendingsPerMonthDTO> listDTOs = List.of(dtos);

        when(genreService.getLendingsAverageDurationPerMonth(any(String.class), any(String.class))).thenReturn(listDTOs);
        when(genreViewMapper.toGenreLendingsAveragePerMonthView(anyList())).thenReturn(views);

        // Act + Assert
        mockMvc.perform(get("/api/genres/lendingsAverageDurationPerMonth")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31")).andDo(result -> { System.out.println(result.getResponse().getContentAsString()); })
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].month").value(1))
                .andExpect(jsonPath("$.items[0].durationAverages[0].value").value(5.0));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetLendingsAverageDurationPerMonthNotFound() throws Exception 
    {
        // Arrange
        when(genreService.getLendingsAverageDurationPerMonth(any(String.class), any(String.class)))
                .thenThrow(new NotFoundException("No genres to show"));

        // Act + Assert
        mockMvc.perform(get("/api/genres/lendingsAverageDurationPerMonth")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-12-31"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.details[0]").value("No genres to show"));
    }


}