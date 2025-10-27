package pt.psoft.g1.psoftg1.lendingmanagement.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.services.CreateLendingRequest;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.lendingmanagement.services.SearchLendingQuery;
import pt.psoft.g1.psoftg1.lendingmanagement.services.SetLendingReturnedRequest;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LendingController.class)
@ContextConfiguration(classes = {LendingController.class, LendingViewMapper.class})
class LendingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReaderService readerService;

    @MockBean
    private LendingService lendingService;

    @MockBean
    private UserService userService;

    @MockBean
    private ConcurrencyService concurrencyService;

    @MockBean
    private LendingViewMapper lendingViewMapper;

    @MockBean
    private Lending lending;

    @MockBean
    private LendingView lendingView;

    @BeforeEach
    void setUp() {
        lending = mock(Lending.class);
        lendingView = mock(LendingView.class);
        when(lending.getLendingNumber()).thenReturn("2025/1");
        when(lending.getVersion()).thenReturn(1L);

        when(lendingView.getLendingNumber()).thenReturn("2025/1");
    }

    // CREATE

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateLending() throws Exception 
    {
        // Arrange
        when(lendingService.create(any(CreateLendingRequest.class))).thenReturn(lending);
        when(lendingViewMapper.toLendingView(any(Lending.class))).thenReturn(lendingView);

        // Act + Assert
        mockMvc.perform(post("/api/lendings")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"9782722203402\", \"readerNumber\": \"2025/1\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentType("application/hal+json"));
    }

    // GETTER
    // @Test
    // @WithMockUser(username = "testuser", roles = {Role.LIBRARIAN})
    // void testFindLendingByNumber() throws Exception 
    // {
    //     Librarian librarian = mock(Librarian.class);

    //     when(lendingService.findByLendingNumber(anyString())).thenReturn(Optional.of(lending));

    //     when(lendingViewMapper.toLendingView(any(Lending.class))).thenReturn(lendingView);
    //     when(userService.getAuthenticatedUser(any())).thenReturn(librarian);

    //     when(lendingService.create(any(CreateLendingRequest.class))).thenReturn(lending);
    //     when(lendingViewMapper.toLendingView(any(Lending.class))).thenReturn(lendingView);

    //     mockMvc.perform(get("/api/lendings/2025/1")
    //                     .with(SecurityMockMvcRequestPostProcessors.csrf()))
    //             .andExpect(status().isOk())
    //             .andExpect(content().contentType("application/hal+json"));

    //     verify(lendingService, times(1)).findByLendingNumber(anyString());
    // }


    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetOverdueLendings_Success() throws Exception 
    {
        // Arrange
        when(lendingService.getOverdue(any(Page.class))).thenReturn(List.of(lending));
        when(lendingViewMapper.toLendingView(anyList())).thenReturn(List.of(lendingView));

        // Act + Assert
        mockMvc.perform(get("/api/lendings/overdue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\": 0, \"size\": 10}")) // Modifique isso de acordo com o seu modelo de `Page`
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetOverdueLendings_NoLendingsFound() throws Exception 
    {
        // Arrange
        // Simulando o comportamento do serviço para retornar uma lista vazia
        when(lendingService.getOverdue(any(Page.class))).thenReturn(Collections.emptyList());

        // Act + Assert
        mockMvc.perform(get("/api/lendings/overdue")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"page\": 0, \"size\": 10}"))
        .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchReaders_Success() throws Exception 
    {
        // Arrange
        when(lendingService.searchLendings(any(Page.class), any(SearchLendingQuery.class))).thenReturn(List.of(lending));
        when(lendingViewMapper.toLendingView(anyList())).thenReturn(List.of(lendingView));

        // Act + Assert
        mockMvc.perform(post("/api/lendings/search")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\": {\"size\": 10, \"number\": 0}, \"query\": {\"isbn\": \"9782722203402\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {Role.READER})
    void testFindLendingByNumberInvalidUser() throws Exception 
    {
        // Arrange
        when(lendingService.findByLendingNumber("2025/002")).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(get("/api/lendings/2025/002")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindLendingByNumberNotFound() throws Exception 
    {
        // Arrange
        when(lendingService.findByLendingNumber("2025/002")).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(get("/api/lendings/2025/002")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSetLendingReturned() throws Exception 
    {
        // Arrange
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");

        ReaderDetails reader = mock(ReaderDetails.class);
        when(reader.getReaderNumber()).thenReturn("2025/1");
        when(lending.getReaderDetails()).thenReturn(reader);
        when(lending.getReaderDetails().getReaderNumber()).thenReturn("2025/1");

        when(lendingService.findByLendingNumber("2025/1")).thenReturn(Optional.of(lending));
        when(lendingService.setReturned(eq("2025/1"), any(SetLendingReturnedRequest.class), anyLong())).thenReturn(lending);
        when(concurrencyService.getVersionFromIfMatchHeader("1")).thenReturn(1L);
        when(lendingViewMapper.toLendingView(any(Lending.class))).thenReturn(lendingView);
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(readerService.findByUsername("testuser")).thenReturn(Optional.of(reader));

        // Act + Assert
        mockMvc.perform(patch("/api/lendings/2025/1")
                        .header("If-Match", "1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commentary\": \"nao gostei\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSetLendingReturnedIfMatchHeaderMissing() throws Exception 
    {
        // Act + Assert
        mockMvc.perform(patch("/api/lendings/2025/1")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"someProperty\": \"value\"}")) // O cabeçalho If-Match não está presente
                .andDo(result -> { System.out.println(result.getResponse().getErrorMessage()); })
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    // Extraia a mensagem de erro da resposta
                    String errorMessage = result.getResponse().getErrorMessage();
                    // Verifique se a mensagem de erro contém a string esperada
                    assertTrue(errorMessage.contains("You must issue a conditional PATCH using 'if-match'"));
                }); // O corpo da resposta está vazio
    }
}
