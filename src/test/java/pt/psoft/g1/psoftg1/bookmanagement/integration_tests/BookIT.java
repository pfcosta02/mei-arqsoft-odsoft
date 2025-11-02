package pt.psoft.g1.psoftg1.bookmanagement.integration_tests;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.isbnmanagement.factory.IsbnProviderFactory;
import pt.psoft.g1.psoftg1.isbnmanagement.infrastructure.IsbnProvider;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.idgeneratormanagement.infrastructure.IdGenerator;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/* Integration test opaque-box do Controller + Service + Domain */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdGenerator idGenerator;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private LendingRepository lendingRepo;

    @MockBean
    private BookRepository bookRepo;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private IsbnProviderFactory isbnProviderFactory;


    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldCreateBookSuccessfully() throws Exception {
        // Simular upload de imagem
        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "fake-image-content".getBytes());

        Isbn isbn = new Isbn("9789722328296");
        IsbnProvider mockProvider = mock(IsbnProvider.class);
        when(isbnProviderFactory.getProvider()).thenReturn(mockProvider);
        when(mockProvider.searchByTitle(anyString())).thenReturn(isbn);

        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.empty());

        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");
        when(authorRepository.findByAuthorNumber(author.getAuthorNumber())).thenReturn(Optional.of(author));

        Genre genre = new Genre("Terror");
        when(genreRepository.findByString(genre.getGenre())).thenReturn(Optional.of(genre));

        when(idGenerator.generateId()).thenReturn("21");

        String title = "Os tres mosqueteiros";
        String description = "Sinceramente nao sei";

        Book savedBook = new Book(isbn.getIsbn(), title, description, genre, List.of(author), "photo.jpg");
        savedBook.setBookId(idGenerator.generateId());

        when(bookRepo.save(any())).thenReturn(savedBook);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books/{isbn}", isbn.getIsbn())
                        .file(photo)
                        .param("title", title)
                        .param("description", description)
                        .param("genre", genre.getGenre())
                        .param("authors[]", "1000")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.bookId").value(idGenerator.generateId()))
                .andExpect(jsonPath("$.isbn").value(isbn.getIsbn()))
                .andExpect(jsonPath("$.title").value(title));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldCreateBookWithoutIsbnSuccessfully() throws Exception {
        // Simular upload de imagem
        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "fake-image-content".getBytes());

        Isbn isbn = new Isbn("9789722328296");
        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.empty());

        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");
        when(authorRepository.findByAuthorNumber(author.getAuthorNumber())).thenReturn(Optional.of(author));

        Genre genre = new Genre("Terror");
        when(genreRepository.findByString(genre.getGenre())).thenReturn(Optional.of(genre));

        when(idGenerator.generateId()).thenReturn("21");

        String title = "Os tres mosqueteiros";
        String description = "Sinceramente nao sei";

        Book savedBook = new Book(isbn.getIsbn(), title, description, genre, List.of(author), "photo.jpg");
        savedBook.setBookId(idGenerator.generateId());

        when(bookRepo.save(any())).thenReturn(savedBook);

        IsbnProvider isbnProvider = mock(IsbnProvider.class);
        when(isbnProviderFactory.getProvider()).thenReturn(isbnProvider);
        when(isbnProvider.searchByTitle(title)).thenReturn(isbn);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books")
                        .file(photo)
                        .param("title", title)
                        .param("description", description)
                        .param("genre", genre.getGenre())
                        .param("authors[]", "1000")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.bookId").value(idGenerator.generateId()))
                .andExpect(jsonPath("$.isbn").value(isbn.getIsbn()))
                .andExpect(jsonPath("$.title").value(title));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldFindBookByIsbnSuccessfully() throws Exception {
        Isbn isbn = new Isbn("9789722328296");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");

        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), null);
        book.setBookId("21");

        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{isbn}", isbn.getIsbn())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"0\""))
                .andExpect(jsonPath("$.isbn").value(isbn.getIsbn()))
                .andExpect(jsonPath("$.title").value("Os tres mosqueteiros"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldDeleteBookPhotoSuccessfully() throws Exception {
        Isbn isbn = new Isbn("9789722328296");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");

        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), "photo.png");
        book.setBookId("21");
        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{isbn}/photo", isbn.getIsbn())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnBookPhotoSuccessfully() throws Exception {
        Isbn isbn = new Isbn("9789722328296");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");

        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), "photo.jpg");
        book.setBookId("21");

        byte[] fakeImage = "fake-image-content".getBytes();
        when(fileStorageService.getFile("photo.jpg")).thenReturn(fakeImage);
        when(fileStorageService.getExtension("photo.jpg")).thenReturn(Optional.of("jpg"));

        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{isbn}/photo", isbn.getIsbn())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(fakeImage));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldUpdateBookSuccessfully() throws Exception {
        Isbn isbn = new Isbn("9789722328296");
        Title title = new Title("Novo Título");
        Description description = new Description("Nova descrição");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");

        Book book = new Book(isbn.getIsbn(), "Antigo Título", "Antiga descrição", genre, List.of(author), null);
        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));

        when(genreRepository.findByString(genre.getGenre())).thenReturn(Optional.of(genre));

        Book updatedBook = new Book(isbn.getIsbn(), title.getTitle(), description.getDescription(), genre, List.of(author), null);

        when(bookRepo.save(book)).thenReturn(updatedBook);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books/{isbn}", isbn)
                        .file(new MockMultipartFile("photo", "", "image/jpeg", new byte[0])) // opcional
                        .param("title", title.getTitle())
                        .param("description", "Nova descrição")
                        .param("genre", "Terror")
                        .param("authors", "1000")
                        .header("If-Match", "0")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"0\""));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnTop5BooksLent_Empty() throws Exception {

        // Simular resultado do repositório
        BookCountDTO dto = mock(BookCountDTO.class);
        List<BookCountDTO> page = List.of(dto);
        when(bookRepo.findTop5BooksLent(Mockito.any(LocalDate.class), Mockito.any(Pageable.class))).thenReturn(page);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/top5")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void test_findBooks() throws Exception
    {
        Isbn isbn = new Isbn("9789722328296");
        Title title = new Title("Novo Título");
        Description description = new Description("Nova descrição");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");

        Book book = new Book(isbn.getIsbn(), "Antigo Título", "Antiga descrição", genre, List.of(author), null);
        when(bookRepo.findByTitle(title.getTitle())).thenReturn(List.of(book));
        when(bookRepo.findByGenre(genre.getGenre())).thenReturn(List.of(book));
        when(bookRepo.findByAuthorName(author.getAuthorNumber() + "%")).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .param("title", title.getTitle())
                        .param("genre", genre.getGenre())
                        .param("authorName", author.getName().getName())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].isbn").value(isbn.getIsbn()))
                .andExpect(jsonPath("$.items[0].title").value(book.getTitle().getTitle()));

    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void test_getAvgLendingDurationByIsbn() throws Exception
    {
        Isbn isbn = new Isbn("9789722328296");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");

        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), null);
        book.setBookId("21");

        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));
        when(lendingRepo.getAvgLendingDurationByIsbn(isbn.getIsbn())).thenReturn(2.0);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{isbn}/avgDuration", isbn.getIsbn())
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.book.title").value(book.getTitle().getTitle()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void test_searchBooks() throws Exception
    {
        Isbn isbn = new Isbn("9789722328296");
        Genre genre = new Genre("Terror");
        Author author = new Author("Pedro", "Qualquer coisa", null);
        author.setAuthorNumber("1000");

        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), null);
        book.setBookId("21");

        when(bookRepo.searchBooks(any(), any())).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/books/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "query": {
                                    "title": "mosqueteiros",
                                    "genre": "Terror",
                                    "authorName": "Pedro"
                                  },
                                  "page": {
                                    "page": 0,
                                    "size": 10
                                  }
                                }
                                """
                        )
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].title").value(book.getTitle().getTitle()));
    }
}