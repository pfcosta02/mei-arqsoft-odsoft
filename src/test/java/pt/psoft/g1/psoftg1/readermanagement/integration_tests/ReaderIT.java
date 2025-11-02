package pt.psoft.g1.psoftg1.readermanagement.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.external.service.ApiNinjasService;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.idgeneratormanagement.infrastructure.IdGenerator;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.PhoneNumber;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderNumber;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReaderIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReaderRepository readerRepo;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private BookRepository bookRepo;

    @MockBean
    private GenreRepository genreRepo;

    @MockBean
    private PhotoRepository photoRepo;

    @MockBean
    private ForbiddenNameRepository forbiddenNameRepo;

    @MockBean
    private LendingRepository lendingRepo;

    @MockBean
    private ApiNinjasService apiNinjasService;

    @MockBean
    private UserService userService;

    @MockBean
    private IdGenerator idGenerator;

    @Test
    @WithMockUser(username = "testuser", roles = {"READER"})
    void shouldGetOwnReaderDataSuccessfully() throws Exception {
        when(idGenerator.generateId()).thenReturn("id1");

        Reader reader = new Reader("testuser","password");
        reader.setName("Pedro Ferreira");
        reader.setUserId("id1");

        Genre genre = new Genre("Terror");
        BirthDate birthDate = mock(BirthDate.class);
        PhoneNumber phoneNumber = mock(PhoneNumber.class);
        ReaderNumber readerNumber = new ReaderNumber("2025/1");
        ReaderDetails readerDetails = new ReaderDetails(readerNumber, reader, birthDate, phoneNumber, true,true,true,"photo", List.of(genre));
        readerDetails.setVersion(1L);

        when(readerRepo.findByUsername("testuser")).thenReturn(Optional.of(readerDetails));
        when(userService.getAuthenticatedUser(any())).thenReturn(reader);

        mockMvc.perform(get("/api/readers").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""))
                .andExpect(jsonPath("$.readerNumber").value("2025/1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
    void shouldGetReaderByNumberSuccessfully() throws Exception {
        // Dados simulados
        Reader reader = new Reader("testuser", "password");
        BirthDate birthDate = mock(BirthDate.class);
        LocalDate birthLocalDate = LocalDate.of(2004, 3, 17);
        when(birthDate.getBirthDate()).thenReturn(birthLocalDate);

        ReaderNumber readerNumber = new ReaderNumber("2025/1");
        ReaderDetails readerDetails = new ReaderDetails(readerNumber, reader, birthDate, mock(PhoneNumber.class), true, true, true, "photo", List.of(new Genre("Terror")));
        readerDetails.setVersion(1L);
        when(readerRepo.findByReaderNumber("2025/1")).thenReturn(Optional.of(readerDetails));

        // Execução
        mockMvc.perform(get("/api/readers/2025/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""))
                .andExpect(jsonPath("$.readerNumber").value("2025/1"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
    void shouldReturnReadersByPhoneNumberSuccessfully() throws Exception {
        String phoneNumber = "999999999";

        // Simular Reader e ReaderDetails
        Reader reader = new Reader("testuser", "password");
        ReaderDetails readerDetails = new ReaderDetails(
                new ReaderNumber("2025/1"),
                reader,
                mock(BirthDate.class),
                mock(PhoneNumber.class),
                true, true, true,
                "photo",
                List.of(new Genre("Terror"))
        );
        readerDetails.setReaderDetailsId("id1");
        when(readerRepo.findByPhoneNumber(phoneNumber)).thenReturn(List.of(readerDetails));

        // Executar e verificar
        mockMvc.perform(get("/api/readers").param("phoneNumber", phoneNumber).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].readerNumber").value("2025/1"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
    void shouldReturnReadersByNameSuccessfully() throws Exception {
        String name = "Pedro";

        // Simular ReaderDetails
        Reader reader = new Reader("testuser", "password");
        reader.setName("Pedro");
        ReaderDetails readerDetails = new ReaderDetails(
                new ReaderNumber("2025/1"),
                reader,
                mock(BirthDate.class),
                mock(PhoneNumber.class),
                true, true, true,
                "photo",
                List.of(new Genre("Terror"))
        );

        when(userService.findByNameLike(name)).thenReturn(List.of(reader));
        when(readerRepo.findByUsername(anyString())).thenReturn(Optional.of(readerDetails));

        mockMvc.perform(get("/api/readers").param("name", name).with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].readerNumber").value("2025/1"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"READER"})
    void shouldReturnReaderPhotoSuccessfullyForLibrarian() throws Exception {
        String readerNumber = "2025/1";
        String photoFile = "photo123.jpg";
        byte[] photoBytes = "fakeImageData".getBytes();

        // Simular ReaderDetails com foto
        Reader reader = new Reader("user", "password");
        reader.setUserId("nao sei");
        ReaderDetails readerDetails = new ReaderDetails(
                new ReaderNumber(readerNumber),
                reader,
                mock(BirthDate.class),
                mock(PhoneNumber.class),
                true, true, true,
                photoFile,
                List.of(new Genre("Terror"))
        );
        readerDetails.setReaderDetailsId("sei la");

        // Mocks
        when(userService.getAuthenticatedUser(any())).thenReturn(reader);
        when(readerRepo.findByUsername(anyString())).thenReturn(Optional.of(readerDetails));
        when(fileStorageService.getFile(photoFile)).thenReturn(photoBytes);
        when(fileStorageService.getExtension(photoFile)).thenReturn(Optional.of("jpg"));

        mockMvc.perform(get("/api/readers/photo").with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(photoBytes));
    }

    @Test
    @WithMockUser(username = "newUser@example.com", roles = {"LIBRARIAN"})
    void shouldCreateReaderSuccessfully() throws Exception {
        // ARRANGE
        MockMultipartFile photoFile = new MockMultipartFile(
                "photo",                         // nome do campo
                "photo.jpg",                      // nome do ficheiro
                MediaType.IMAGE_JPEG_VALUE,
                "fakeImageData".getBytes()
        );

        // Criar um ReaderDetails de retorno simulado
        Reader reader = new Reader("newUser@example.com", "password");
        reader.setUserId("user-123");
        Genre genre = new Genre("Terror");
        ReaderDetails savedDetails = new ReaderDetails(
                new ReaderNumber("2025/1"),
                reader,
                mock(BirthDate.class),
                mock(PhoneNumber.class),
                true, true, true,
                "photo.jpg",
                List.of(genre)
        );
        savedDetails.setReaderDetailsId("rd-123");
        savedDetails.setVersion(1L);

        // Mock dos serviços
        when(fileStorageService.getRequestPhoto(any())).thenReturn("photo.jpg");
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        when(forbiddenNameRepo.findByForbiddenNameIsContained(anyString())).thenReturn(List.of());
        when(readerRepo.getCountFromCurrentYear()).thenReturn(2);
        when(readerRepo.save(any())).thenReturn(savedDetails);
        when(genreRepo.findByString("Terror")).thenReturn(Optional.of(genre));

        // ACT
        mockMvc.perform(multipart("/api/readers")
                        .file(photoFile)
                        .param("username", "newUser@example.com")
                        .param("password", "password123")
                        .param("fullName", "John Doe")
                        .param("birthDate", "1990-01-01")
                        .param("phoneNumber", "911234567")
                        .param("interestList", "Terror")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("ETag", "\"1\""))
                .andExpect(header().string("Location", containsString("/api/readers/2025/1")))
                .andExpect(jsonPath("$.readerNumber").value("2025/1"))
                .andExpect(jsonPath("$.email").value("newUser@example.com"))
                .andExpect(jsonPath("$.photo").value("http://localhost/api/readers/2025/1/photo"));
    }

}