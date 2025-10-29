//package pt.psoft.g1.psoftg1.bookmanagement.integration_tests;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.AuthorRepositoryRelationalImpl;
//import pt.psoft.g1.psoftg1.authormanagement.model.Author;
//import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
//import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational.BookRepositoryRelationalImpl;
//import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
//import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
//import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
//import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational.GenreRepositoryRelationalImpl;
//import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
//import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGenerator;
//import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational.ReaderDetailsRepositoryRelationalImpl;
//import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational.PhotoRepositoryRelationalImpl;
//import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
///* Integration test opaque-box do Controller + Service + Domain */
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//class BookIT {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private FileStorageService fileStorageService;
//
////    @MockBean
////    private ISBNbyTitleAdapter isbnAdapter;
//
//    @MockBean
//    private IdGenerator idGenerator;
//
//    @MockBean
//    private GenreRepositoryRelationalImpl genreRepository;
//
//    @MockBean
//    private AuthorRepositoryRelationalImpl authorRepository;
//
//    @MockBean
//    private PhotoRepositoryRelationalImpl photoRepository;
//
//    @MockBean
//    private ReaderDetailsRepositoryRelationalImpl readerRepository;
//
//    @MockBean
//    private BookEntityMapper bookEntityMapper;
//
//    @MockBean
//    private BookRepositoryRelationalImpl bookRepo;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
//    void shouldCreateBookSuccessfully() throws Exception {
//        // Simular upload de imagem
//        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "fake-image-content".getBytes());
//
//        Isbn isbn = new Isbn("9789722328296");
//        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.empty());
//
//        Author author = new Author("Pedro", "Qualquer coisa", null);
//        author.setAuthorNumber("1000");
//        when(authorRepository.findByAuthorNumber(author.getAuthorNumber())).thenReturn(Optional.of(author));
//
//        Genre genre = new Genre("Terror");
//        when(genreRepository.findByString(genre.getGenre())).thenReturn(Optional.of(genre));
//
//        when(idGenerator.generateId()).thenReturn("21");
//
//        String title = "Os tres mosqueteiros";
//        String description = "Sinceramente nao sei";
//
//
//        Book savedBook = new Book(isbn.getIsbn(), title, description, genre, List.of(author), "photo.jpg");
//        savedBook.setBookId(idGenerator.generateId());
//
//        when(bookRepo.save(any())).thenReturn(savedBook);
//
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/books/{isbn}", isbn.getIsbn())
//                    .file(photo)
//                    .param("title", title)
//                    .param("description", description)
//                    .param("genre", genre.getGenre())
//                    .param("authors[]", "1000")
//                    .with(SecurityMockMvcRequestPostProcessors.csrf())
//                    .with(request -> {
//                        request.setMethod("PUT");
//                        return request;
//                    }))
//                .andExpect(status().isCreated())
//                .andExpect(header().exists("Location"))
//                .andExpect(jsonPath("$.isbn").value(isbn.getIsbn()))
//                .andExpect(jsonPath("$.title").value(title));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
//    void shouldFindBookByIsbnSuccessfully() throws Exception {
//        Isbn isbn = new Isbn("9789722328296");
//        Genre genre = new Genre("Terror");
//        Author author = new Author("Pedro", "Qualquer coisa", null);
//        author.setAuthorNumber("1000");
//
//        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), null);
//        book.setBookId("21");
//
//        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{isbn}", isbn.getIsbn())
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk())
//                .andExpect(header().string("ETag", "\"0\""))
//                .andExpect(jsonPath("$.isbn").value(isbn.getIsbn()))
//                .andExpect(jsonPath("$.title").value("Os tres mosqueteiros"));
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
//    void shouldDeleteBookPhotoSuccessfully() throws Exception {
//        Isbn isbn = new Isbn("9789722328296");
//        Genre genre = new Genre("Terror");
//        Author author = new Author("Pedro", "Qualquer coisa", null);
//        author.setAuthorNumber("1000");
//
//        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), "photo.jpg");
//        book.setBookId("21");
//        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/{isbn}/photo", isbn.getIsbn())
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
//    void shouldReturnBookPhotoSuccessfully() throws Exception {
//        Isbn isbn = new Isbn("9789722328296");
//        Genre genre = new Genre("Terror");
//        Author author = new Author("Pedro", "Qualquer coisa", null);
//        author.setAuthorNumber("1000");
//
//        Book book = new Book(isbn.getIsbn(), "Os tres mosqueteiros", "Sinceramente nao sei", genre, List.of(author), "photo.jpg");
//        book.setBookId("21");
//
//        byte[] fakeImage = "fake-image-content".getBytes();
//
//        when(bookRepo.findByIsbn(isbn.getIsbn())).thenReturn(Optional.of(book));
//        when(fileStorageService.getFile("photo.jpg")).thenReturn(fakeImage);
//        when(fileStorageService.getExtension("photo.jpg")).thenReturn(Optional.of("jpg"));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/{isbn}/photo", isbn.getIsbn())
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
//                .andExpect(content().bytes(fakeImage));
//    }
//
//    // @Test
//    // @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
//    // void shouldUpdateBookSuccessfully() throws Exception {
//    //     String isbn = "9789722328296";
//    //     String ifMatch = "1";
//    //     String updatedTitle = "Novo Título";
//
//    //     Author author = new Author("Pedro", "Qualquer coisa", null);
//    //     author.setAuthorNumber("1000");
//    //     Genre genre = new Genre("Terror");
//
//    //     Book updatedBook = new Book(isbn, updatedTitle, "Nova descrição", genre, List.of(author), null);
//    //     updatedBook.setId("21");
//
//    //     when(concurrencyService.getVersionFromIfMatchHeader(ifMatch)).thenReturn(1L);
//    //     when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));
//    //     when(genreRepository.findByString("Terror")).thenReturn(Optional.of(genre));
//    //     when(bookRepo.findByIsbn(isbn)).thenReturn(Optional.of(updatedBook));
//    //     when(bookRepo.save(Mockito.any(Book.class))).thenReturn(updatedBook);
//
//    //     mockMvc.perform(MockMvcRequestBuilders.patch("/api/books/{isbn}", isbn)
//    //             .header("If-Match", ifMatch)
//    //             .param("title", updatedTitle)
//    //             .param("description", "Nova descrição")
//    //             .param("genre", "Terror")
//    //             .param("authors", "1000")
//    //             .with(SecurityMockMvcRequestPostProcessors.csrf()))
//    //             .andExpect(status().isOk())
//    //             .andExpect(header().string("ETag", "2"))
//    //             .andExpect(jsonPath("$.title").value(updatedTitle));
//    // }
//
//    @Test
//    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
//    void shouldReturnTop5BooksLent() throws Exception {
//        Author author = new Author("Pedro", "Qualquer coisa", null);
//        author.setAuthorNumber("1000");
//        Book book = new Book("9789722328296", "Os tres mosqueteiros", "Descrição", new Genre("Terror"), List.of(author), null);
//        book.setBookId("21");
//
//        // Simular resultado do repositório
//        BookCountDTO dto = mock(BookCountDTO.class);
//        List<BookCountDTO> list = List.of(dto);
//        when(bookRepo.findTop5BooksLent(Mockito.any(LocalDate.class), Mockito.any(Pageable.class))).thenReturn(list);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/top5")
//                .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.items[0].title").value(book.getTitle().getTitle()));
//    }
//}
