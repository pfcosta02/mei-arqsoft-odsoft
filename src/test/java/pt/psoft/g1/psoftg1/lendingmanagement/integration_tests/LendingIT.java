package pt.psoft.g1.psoftg1.lendingmanagement.integration_tests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.idgeneratormanagement.infrastructure.IdGenerator;
import pt.psoft.g1.psoftg1.isbnmanagement.factory.IsbnProviderFactory;
import pt.psoft.g1.psoftg1.isbnmanagement.infrastructure.IsbnProvider;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.services.CreateLendingRequest;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class LendingIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdGenerator idGenerator;

    @MockBean
    private LendingRepository lendingRepo;

    @MockBean
    private BookRepository bookRepo;

    @MockBean
    private ReaderRepository readerRepo;

    @MockBean
    private AuthorRepository authorRepo;

    @MockBean
    private GenreRepository genreRepo;

    @MockBean
    private IsbnProviderFactory isbnProviderFactory;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private UserService userService;


    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldCreateLendingSuccessfully() throws Exception {
        CreateLendingRequest lendingRequest = new CreateLendingRequest("9789722328296", "123456");

        Isbn isbn = new Isbn("9789722328296");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");
        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "description", genre, List.of(author), "photo.jpg");
        ReaderDetails reader = mock(ReaderDetails.class);
        Lending lending = new Lending(book, reader, 1, 14, 50);
        lending.setLendingId("LEND123");

        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));
        when(readerRepo.findByReaderNumber("123456")).thenReturn(Optional.of(reader));
        when(lendingRepo.getCountFromCurrentYear()).thenReturn(0);
        when(idGenerator.generateId()).thenReturn("LEND123");
        when(lendingRepo.save(any())).thenReturn(lending);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/lendings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {
                "isbn": "9789722328296",
                "readerNumber": "123456"
            }
        """)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.lendingId").value(idGenerator.generateId()))
                .andExpect(jsonPath("$.bookTitle").value(lending.getBook().getTitle().getTitle()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldGetLendingByLendingNumberSuccessfully() throws Exception {
        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn(new Title("Os tres mosqueteiros"));
        when(book.getIsbn()).thenReturn(new Isbn("9789722328296"));
        ReaderDetails reader = mock(ReaderDetails.class);

        Lending lending = mock(Lending.class);
        when(lending.getLendingNumber()).thenReturn("2025/1");
        when(lending.getVersion()).thenReturn(0L);

        when(lending.getReaderDetails()).thenReturn(reader);
        when(lending.getBook()).thenReturn(book);


        Librarian librarian = mock(Librarian.class);
        when(userService.getAuthenticatedUser(any())).thenReturn(librarian);

        when(lendingRepo.findByLendingNumber("2025/1")).thenReturn(Optional.of(lending));

        mockMvc.perform(get("/api/lendings/2025/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"0\""))
                .andExpect(jsonPath("$.lendingNumber").value("2025/1"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READER"})
    void shouldSetLendingAsReturnedSuccessfully() throws Exception {
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        when(readerDetails.getReaderNumber()).thenReturn("123456");

        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn(new Title("Os tres mosqueteiros"));
        when(book.getIsbn()).thenReturn(new Isbn("9789722328296"));

        Lending lending = mock(Lending.class);
        when(lending.getLendingNumber()).thenReturn("2025/1");
        when(lending.getVersion()).thenReturn(1L);
        when(lending.getReaderDetails()).thenReturn(readerDetails);
        when(lending.getBook()).thenReturn(book);

        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");


        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(readerRepo.findByUsername("testuser")).thenReturn(Optional.of(readerDetails));
        when(lendingRepo.findByLendingNumber("2025/1")).thenReturn(Optional.of(lending));
        when(lendingRepo.save(any())).thenReturn(lending);

        mockMvc.perform(patch("/api/lendings/2025/1")
                        .header("If-Match", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "commentary": "Returned in good condition"
                }
            """)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnAverageLendingDuration() throws Exception {
        when(lendingRepo.getAverageDuration()).thenReturn(5.0);

        mockMvc.perform(get("/api/lendings/avgDuration")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lendingsAverageDuration").value(5.0));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnOverdueLendings() throws Exception {
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        when(readerDetails.getReaderNumber()).thenReturn("123456");

        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn(new Title("Os tres mosqueteiros"));
        when(book.getIsbn()).thenReturn(new Isbn("9789722328296"));

        Lending lending = mock(Lending.class);
        when(lending.getLendingNumber()).thenReturn("2025/1");
        when(lending.getVersion()).thenReturn(1L);
        when(lending.getReaderDetails()).thenReturn(readerDetails);
        when(lending.getBook()).thenReturn(book);

        when(lendingRepo.getOverdue(any())).thenReturn(List.of(lending));

        mockMvc.perform(get("/api/lendings/overdue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "page": 0,
                    "size": 10
                }
            """)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldSearchLendingsSuccessfully() throws Exception {
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        when(readerDetails.getReaderNumber()).thenReturn("123456");

        Book book = mock(Book.class);
        when(book.getTitle()).thenReturn(new Title("Os tres mosqueteiros"));
        when(book.getIsbn()).thenReturn(new Isbn("9789722328296"));

        Lending lending = mock(Lending.class);
        when(lending.getLendingNumber()).thenReturn("2025/1");
        when(lending.getVersion()).thenReturn(1L);
        when(lending.getReaderDetails()).thenReturn(readerDetails);
        when(lending.getBook()).thenReturn(book);

        when(lendingRepo.searchLendings(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(lending));


        mockMvc.perform(MockMvcRequestBuilders.post("/api/lendings/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
            {                
                "query": {
                    "readerNumber": "123456",
                    "isbn": "9789722328296",
                    "returned": false,
                    "startDate": "2025-10-01",
                    "endDate": "2025-11-01"
                },
                "page": {
                    "page": 0,
                    "size": 10
                }                            
            }
        """)
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}