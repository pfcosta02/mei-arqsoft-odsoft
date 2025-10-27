package pt.psoft.g1.psoftg1.readermanagement.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingViewMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.services.CreateReaderRequest;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
import pt.psoft.g1.psoftg1.usermanagement.model.Role;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;
import pt.psoft.g1.psoftg1.external.service.ApiNinjasService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReaderController.class)
class ReaderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReaderService readerService;

    @MockBean
    private UserService userService;

    @MockBean
    private ReaderViewMapper readerViewMapper;

    @MockBean
    private LendingService lendingService;

    @MockBean
    private LendingViewMapper lendingViewMapper;

    @MockBean
    private ConcurrencyService concurrencyService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private ApiNinjasService apiNinjasService;

    private User user;
    private ReaderDetails readerDetails;
    private ReaderView readerView;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = mock(User.class);
        readerDetails = mock(ReaderDetails.class);
        readerView = mock(ReaderView.class);

        when(user.getUsername()).thenReturn("reader@example.com");
        when(readerDetails.getVersion()).thenReturn(1L);
    }

    @Test
    @WithMockUser(username = "reader@example.com", roles = {"USER"})
    void testGetDataAsReader() throws Exception {
        // Arrange
        Role role = mock(Role.class);
        when(user.getAuthorities()).thenReturn(Set.of(role));
        when(userService.getAuthenticatedUser(any(Authentication.class))).thenReturn(user);
        when(readerService.findByUsername("reader@example.com")).thenReturn(Optional.of(readerDetails));
        when(readerViewMapper.toReaderView(readerDetails)).thenReturn(readerView);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "librarian@example.com", roles = {"LIBRARIAN"})
    void testGetDataAsLibrarian() throws Exception {
        // Arrange
        Set<Role> mockedRoles = mock(Set.class);
        when(mockedRoles.contains(any(Role.class))).thenReturn(true);
        when(user.getAuthorities()).thenReturn(mockedRoles);

        when(userService.getAuthenticatedUser(any(Authentication.class))).thenReturn(user);
        when(readerService.findAll()).thenReturn(List.of(readerDetails));
        when(readerViewMapper.toReaderView(List.of(readerDetails))).thenReturn(List.of(readerView));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"LIBRARIAN"})
    void testFindByReaderNumber() throws Exception {
        // Arrange
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        ReaderQuoteView quoteView = mock(ReaderQuoteView.class);
        BirthDate birthDate = mock(BirthDate.class);

        when(readerDetails.getBirthDate()).thenReturn(birthDate);
        when(birthDate.getBirthDate()).thenReturn(LocalDate.now());
        when(readerDetails.getVersion()).thenReturn(1L);

        when(readerService.findByReaderNumber("1990/1")).thenReturn(Optional.of(readerDetails));
        when(readerViewMapper.toReaderQuoteView(readerDetails)).thenReturn(quoteView);
        when(apiNinjasService.getRandomEventFromYearMonth(1990, 5)).thenReturn("Some quote");

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers/1990/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void testFindByPhoneNumber() throws Exception 
    {
        // Arrange
        when(readerService.findByPhoneNumber("912345678")).thenReturn(List.of(readerDetails));
        when(readerViewMapper.toReaderView(List.of(readerDetails))).thenReturn(List.of(readerView));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers")
                        .param("phoneNumber", "912345678"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"LIBRARIAN"})
    void testFindByReaderName() throws Exception {
        // Arrange
        User user1 = mock(User.class);
        when(user1.getUsername()).thenReturn("reader@example.com");

        when(userService.findByNameLike("Ana")).thenReturn(List.of(user1));
        when(readerService.findByUsername("reader@example.com")).thenReturn(Optional.of(readerDetails));
        when(readerViewMapper.toReaderView(List.of(readerDetails))).thenReturn(List.of(readerView));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers")
                        .param("name", "Ana"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "reader@example.com", roles = {"USER"})
    void testGetSpecificReaderPhoto() throws Exception 
    {
        // Arrange
        Photo photo = mock(Photo.class);
        when(photo.getPhotoFile()).thenReturn("photo.jpg");

        when(user.getUsername()).thenReturn("reader@example.com");
        when(user.getAuthorities()).thenReturn(Set.of(new Role(Role.READER)));
        when(userService.getAuthenticatedUser(any(Authentication.class))).thenReturn(user);

        when(readerDetails.getReaderNumber()).thenReturn("1990/1");
        when(readerDetails.getPhoto()).thenReturn(photo);
        when(readerService.findByUsername("reader@example.com")).thenReturn(Optional.of(readerDetails));
        when(readerService.findByReaderNumber("1990/1")).thenReturn(Optional.of(readerDetails));
        when(fileStorageService.getFile("photo.jpg")).thenReturn(new byte[]{1, 2, 3});
        when(fileStorageService.getExtension("photo.jpg")).thenReturn(Optional.of("jpg"));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers/1990/1/photo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser(username = "reader@example.com", roles = {"USER"})
    void testGetReaderOwnPhoto() throws Exception 
    {
        // Arrange
        Photo photo = mock(Photo.class);
        when(photo.getPhotoFile()).thenReturn("photo.jpg");

        when(user.getUsername()).thenReturn("reader@example.com");
        when(userService.getAuthenticatedUser(any(Authentication.class))).thenReturn(user);
        when(readerDetails.getPhoto()).thenReturn(photo);
        when(readerService.findByUsername("reader@example.com")).thenReturn(Optional.of(readerDetails));
        when(fileStorageService.getFile("photo.jpg")).thenReturn(new byte[]{1, 2, 3});
        when(fileStorageService.getExtension("photo.jpg")).thenReturn(Optional.of("jpg"));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers/photo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }
    
    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void testCreateReader() throws Exception 
    {
        // Arrange
        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "image".getBytes());

        when(fileStorageService.getRequestPhoto(photo)).thenReturn("photo.jpg");
        when(readerService.create(any(CreateReaderRequest.class), eq("photo.jpg"))).thenReturn(readerDetails);
        when(readerViewMapper.toReaderView(readerDetails)).thenReturn(readerView);
        when(readerDetails.getReaderNumber()).thenReturn("1990/1");
        when(readerDetails.getVersion()).thenReturn(1L);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/readers")
                        .file(photo)
                        .param("username", "john.doe@example.com")
                        .param("password", "securePassword123")
                        .param("fullName", "John Doe")
                        .param("birthDate", "1990-01-01")
                        .param("phoneNumber", "912345678")
                        .param("gdpr", "true")
                        .param("marketing", "false")
                        .param("thirdParty", "false")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteReaderPhoto() throws Exception 
    {
        // Arrange
        Photo photo = mock(Photo.class);
        when(photo.getPhotoFile()).thenReturn("photo.jpg");
        when(readerDetails.getPhoto()).thenReturn(photo);

        when(userService.getAuthenticatedUser(any(Authentication.class))).thenReturn(user);
        when(readerService.findByUsername("reader@example.com")).thenReturn(Optional.of(readerDetails));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/readers/photo")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READER"})
    void testUpdateReader() throws Exception 
    {
        // Arrange
        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "image".getBytes());

        when(fileStorageService.getRequestPhoto(photo)).thenReturn("photo.jpg");
        when(userService.getAuthenticatedUser(any(Authentication.class))).thenReturn(user);
        when(concurrencyService.getVersionFromIfMatchHeader("1")).thenReturn(1L);
        when(readerService.update(any(), any(), eq(1L), eq("photo.jpg"))).thenReturn(readerDetails);
        when(readerViewMapper.toReaderView(readerDetails)).thenReturn(readerView);
        when(readerDetails.getVersion()).thenReturn(1L);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/readers")
                        .file(photo)
                        .param("fullName", "John Doe")
                        .param("phoneNumber", "912345678")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(request -> {
                            request.setMethod("PATCH"); // multipart defaults to POST
                            return request;
                        })
                        .header("If-Match", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "reader@example.com", roles = {"USER"})
    void testGetReaderLendings() throws Exception 
    {
        // Arrange
        when(readerService.findByReaderNumber("2020/1")).thenReturn(Optional.of(readerDetails));
        when(userService.getAuthenticatedUser(any(Authentication.class))).thenReturn(user);
        when(readerService.findByUsername("reader@example.com")).thenReturn(Optional.of(readerDetails));
        when(readerDetails.getReaderNumber()).thenReturn("2020/1");
        when(lendingService.listByReaderNumberAndIsbn("2020/1", "1234567890", Optional.empty())).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers/2020/1/lendings")
                        .param("isbn", "1234567890"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTopReaders() throws Exception 
    {
        // Arrange
        when(readerService.findTopReaders(5)).thenReturn(List.of(readerDetails));
        when(readerViewMapper.toReaderView(List.of(readerDetails))).thenReturn(List.of(readerView));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers/top5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTop5ReaderByGenre() throws Exception 
    {
        // Arrange
        when(readerService.findTopByGenre("Fantasy", LocalDate.parse("2020-01-01"), LocalDate.parse("2020-12-31"))).thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/readers/top5ByGenre")
                        .param("genre", "Fantasy")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-12-31"))
                .andExpect(status().isNotFound());
    }

   @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchReaders() throws Exception 
    {
        when(readerService.searchReaders(any(), any())).thenReturn(List.of(readerDetails));
        when(readerViewMapper.toReaderView(List.of(readerDetails))).thenReturn(List.of(readerView));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/readers/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content("{}")) 
                .andExpect(status().isOk());
    }

}
