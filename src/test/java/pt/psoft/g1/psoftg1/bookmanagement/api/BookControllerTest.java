package pt.psoft.g1.psoftg1.bookmanagement.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookService;
import pt.psoft.g1.psoftg1.bookmanagement.services.CreateBookRequest;
import pt.psoft.g1.psoftg1.bookmanagement.services.UpdateBookRequest;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LendingService lendingService;

    @MockBean
    private UserService userService;

    @MockBean
    private ReaderService readerService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private BookViewMapper bookViewMapper;

    @MockBean
    private ConcurrencyService concurrencyService;

//    @MockBean
//    private ISBNbyTitleAdapter isbnByTitleService;

    private Book book;

    private BookView bookView;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Title title = mock(Title.class);
        when(title.getTitle()).thenReturn("Test Book");

        Isbn isbn = mock(Isbn.class);
        when(isbn.getIsbn()).thenReturn("123456789");

        book = mock(Book.class);
        when(book.getIsbn()).thenReturn(isbn);
        when(book.getTitle()).thenReturn(title);
        when(book.getPhoto()).thenReturn(null);

        bookView = mock(BookView.class);
        when(bookView.getIsbn()).thenReturn("123456789");
        when(bookView.getTitle()).thenReturn("Test Book");
    }

    // Create

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateBook() throws Exception 
    {
        // Arrange
        CreateBookRequest createRequest = mock(CreateBookRequest.class); 
        when(createRequest.getTitle()).thenReturn("Test Book");

        String isbn = "123456789"; 

        when(bookService.create(any(CreateBookRequest.class), eq(isbn))).thenReturn(book);
        when(bookViewMapper.toBookView(book)).thenReturn(bookView);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{isbn}", "123456789")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Book\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    // @Test
    // @WithMockUser(username = "testuser", roles = {"USER"})
    // void testCreateBookWithPhoto() throws Exception 
    // {
    //     // Arrange
    //     CreateBookRequest createRequest = mock(CreateBookRequest.class);
    //     verify(createRequest).setPhotoURI(null);
        
    //     MockMultipartFile file = new MockMultipartFile("photo", "test.png", "image/png", "fakeimage".getBytes());
    //     when(createRequest.getPhoto()).thenReturn(file);
    //     when(fileStorageService.getRequestPhoto(file)).thenReturn("fileName.png");
        

    //     /* Quando entra no if */
    //     verify(createRequest).setPhotoURI("fileName.png");
        
    //     when(bookService.create(any(CreateBookRequest.class), anyString())).thenReturn(book);
    //     when(bookViewMapper.toBookView(book)).thenReturn(bookView);

    //     mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{isbn}", "123456789")
    //                     .with(SecurityMockMvcRequestPostProcessors.csrf())
    //                     .contentType(MediaType.MULTIPART_FORM_DATA))
    //             .andExpect(status().isCreated());
    // }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateBookThrowsException() throws Exception 
    {
        // Arrange
        when(bookService.create(any(CreateBookRequest.class), anyString()))
                .thenThrow(new RuntimeException("error"));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/books/{isbn}", "123456789")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest());
    }

    // GETTERS

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindByIsbn() throws Exception 
    {
        // Arrange
        when(bookService.findByIsbn("123456789")).thenReturn(book);
        when(bookViewMapper.toBookView(book)).thenReturn(bookView);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/123456789")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetSpecificBookPhoto_NoPhoto() throws Exception 
    {
        // Arrange
        when(bookService.findByIsbn("123456789")).thenReturn(book);
        when(book.getPhoto()).thenReturn(null);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/123456789/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetSpecificBookPhoto_ImageNull() throws Exception 
    {
        // Arrange
        when(bookService.findByIsbn("123456789")).thenReturn(book);

        Photo photo = mock(Photo.class);
        when(book.getPhoto()).thenReturn(photo);
        
        when(photo.getPhotoFile()).thenReturn("test.png");
        
        when(fileStorageService.getFile("test.png")).thenReturn(null);
        when(fileStorageService.getExtension("test.png")).thenReturn(Optional.of("png"));
        
        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/123456789/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetSpecificBookPhoto_PngImage() throws Exception 
    {
        // Arrange
        Photo photo = mock(Photo.class);
        when(photo.getPhotoFile()).thenReturn("test.png");
        when(book.getPhoto()).thenReturn(photo);
        when(bookService.findByIsbn("123456789")).thenReturn(book);
        when(fileStorageService.getFile("test.png")).thenReturn("imagebytes".getBytes());
        when(fileStorageService.getExtension("test.png")).thenReturn(Optional.of("png"));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/123456789/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindBooksByGenreAndAuthor() throws Exception 
    {
        // Arrange
        when(bookService.findByGenre("Fiction")).thenReturn(List.of(book));
        when(bookService.findByAuthorName("John")).thenReturn(List.of(book));
        when(bookViewMapper.toBookView(anyList())).thenReturn(List.of(bookView));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .param("genre", "Fiction")
                        .param("authorName", "John")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetBooksSuggestions_ReaderNotFound() throws Exception 
    {
        // Arrage
        User user = mock(User.class);
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(user.getUsername()).thenReturn("unknown");
        when(readerService.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/suggestions")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindBooks() throws Exception 
    {
        // Arrange
        List<Book> mockBooks = List.of(book);
        List<BookView> mockBookViews = List.of(bookView);

        when(bookService.findByTitle("Test Book")).thenReturn(mockBooks);
        when(bookViewMapper.toBookView(mockBooks)).thenReturn(mockBookViews);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books")
                        .param("title", "Test Book")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].isbn").value("123456789"))  // Corrigido para usar "items"
                .andExpect(jsonPath("$.items[0].title").value("Test Book"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTop5BooksLent() throws Exception 
    {
        // Arrange
        BookView bookView = mock(BookView.class);
        when(bookView.getIsbn()).thenReturn("12345");
        when(bookView.getTitle()).thenReturn("Popular Book");
        BookCountView bookCountViewDouble = mock(BookCountView.class);
        when(bookCountViewDouble.getLendingCount()).thenReturn(15L);
        when(bookCountViewDouble.getBookView()).thenReturn(bookView);
        BookCountDTO bookCountDTO = mock(BookCountDTO.class);
        when(bookCountDTO.getLendingCount()).thenReturn(15L);


        List<BookCountView> mockBookCountViews = List.of(bookCountViewDouble);

        when(bookService.findTop5BooksLent()).thenReturn(List.of(bookCountDTO));
        when(bookViewMapper.toBookCountView(anyList())).thenReturn(mockBookCountViews);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/top5")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].bookView.isbn").value("12345"))  // Ajuste do caminho
                .andExpect(jsonPath("$.items[0].bookView.title").value("Popular Book")) // Ajuste do caminho
                .andExpect(jsonPath("$.items[0].lendingCount").value(15)); // Ajuste do caminho
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetBooksSuggestions() throws Exception 
    {
        // Arrange
        ReaderDetails readerDetails = mock(ReaderDetails.class);
        when(readerDetails.getReaderNumber()).thenReturn("1");
        User user = mock(User.class);
        when(userService.getAuthenticatedUser(any())).thenReturn(user);
        when(user.getUsername()).thenReturn("testuser");
        when(readerService.findByUsername("testuser")).thenReturn(Optional.of(readerDetails));
        when(bookService.getBooksSuggestionsForReader("1")).thenReturn(List.of(book));
        when(bookViewMapper.toBookView(anyList())).thenReturn(List.of(bookView));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/suggestions")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].isbn").value("123456789"))
                .andExpect(jsonPath("$.items[0].title").value("Test Book"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAvgLendingDurationByIsbn() throws Exception 
    {
        // Arrange
        Double avgDuration = 7.5;

        BookAverageLendingDurationView bookAvgLendingDurationView = mock(BookAverageLendingDurationView.class);
        when(bookAvgLendingDurationView.getBook()).thenReturn(bookView);
        when(bookAvgLendingDurationView.getAverageLendingDuration()).thenReturn(avgDuration);

        when(bookService.findByIsbn("123456789")).thenReturn(book);
        when(lendingService.getAvgLendingDurationByIsbn("123456789")).thenReturn(avgDuration);
        when(bookViewMapper.toBookAverageLendingDurationView(book, avgDuration))
                .thenReturn(bookAvgLendingDurationView);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books/123456789/avgDuration")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk()).andDo(result -> System.out.println(result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.book.isbn").value("123456789"))
                .andExpect(jsonPath("$.book.title").value("Test Book"))
                .andExpect(jsonPath("$.averageLendingDuration").value(7.5));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSearchBooks() throws Exception 
    {
        // Arrange
        String searchRequestJson = "{ \"title\": \"Test Book\", \"author\": \"Test Author\", \"genre\": \"Fiction\" }";
        List<Book> mockBooks = List.of(book);
        List<BookView> mockBookViews = List.of(bookView);

        when(bookService.searchBooks(any(),any())).thenReturn(mockBooks);
        when(bookViewMapper.toBookView(mockBooks)).thenReturn(mockBookViews);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/books/search")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(searchRequestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].isbn").value("123456789"))
                .andExpect(jsonPath("$.items[0].title").value("Test Book"));
    }

    // UPDATE

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateBook() throws Exception 
    {
        // Arrange
        UpdateBookRequest updateRequest = mock(UpdateBookRequest.class);
        when(updateRequest.getIsbn()).thenReturn("123456789");

        when(bookService.update(any(UpdateBookRequest.class), anyString())).thenReturn(book);
        when(book.getVersion()).thenReturn(1L);
        when(bookViewMapper.toBookView(book)).thenReturn(bookView);
        when(concurrencyService.getVersionFromIfMatchHeader("1")).thenReturn(1L);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/books/123456789")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isbn\": \"123456789\"}")
                        .header("If-Match", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testUpdateBookWithPhoto() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("photo", "test.jpg", "image/jpeg", "data".getBytes());
        when(fileStorageService.getRequestPhoto(file)).thenReturn("fileName.jpg");
        when(concurrencyService.getVersionFromIfMatchHeader("1")).thenReturn(1L);
        when(bookService.update(any(UpdateBookRequest.class), anyString())).thenReturn(book);
        when(book.getVersion()).thenReturn(2L);
        when(bookViewMapper.toBookView(book)).thenReturn(bookView);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/books/123456789")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .header("If-Match", "1")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"2\""));
    }

    // DELETE

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testDeleteBookPhotoNoPhoto() throws Exception 
    {
        // Arrange
        when(bookService.findByIsbn("123456789")).thenReturn(book);
        when(book.getPhoto()).thenReturn(null);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/123456789/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());


    }

    // @Test
    // @WithMockUser(username = "testuser", roles = {"USER"})
    // void testDeleteBookPhoto() throws Exception {
    //     // Arrange
    //     Photo photo = mock(Photo.class);
    //     when(photo.getPhotoFile()).thenReturn("test.jpg");

    //     Isbn isbn = mock(Isbn.class);

    //     when(bookService.findByIsbn("123456789")).thenReturn(book);
    //     when(book.getPhoto()).thenReturn(photo);
    //     when(book.getVersion()).thenReturn(1L);
    //     when(book.getIsbn()).thenReturn(isbn); 

    //     // Act
    //     mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/123456789/photo")
    //                     .with(SecurityMockMvcRequestPostProcessors.csrf()))
    //             .andExpect(status().isOk());

    //     // Assert
    //     verify(fileStorageService).deleteFile("test.jpg");
    //     verify(bookService).removeBookPhoto(eq("123456789"), eq(1L));
    // }
}
