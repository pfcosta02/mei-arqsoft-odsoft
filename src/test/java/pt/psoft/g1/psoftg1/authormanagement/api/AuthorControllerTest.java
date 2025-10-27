package pt.psoft.g1.psoftg1.authormanagement.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorService;
import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;
import pt.psoft.g1.psoftg1.authormanagement.services.UpdateAuthorRequest;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookView;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.services.ConcurrencyService;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private AuthorViewMapper authorViewMapper;

    @MockBean
    private ConcurrencyService concurrencyService;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private BookViewMapper bookViewMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testCreateAuthor() throws Exception {
        CreateAuthorRequest createRequest = new CreateAuthorRequest();
        createRequest.setName("Author Name");

        Author author = mock(Author.class);
        author.setAuthorNumber("AUTH123");


        when(authorService.create(any(CreateAuthorRequest.class))).thenReturn(author);
        when(authorViewMapper.toAuthorView(author)).thenReturn(new AuthorView());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authors")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Author Name\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testPartialUpdate() throws Exception {
        UpdateAuthorRequest updateRequest = new UpdateAuthorRequest();
        updateRequest.setName("Updated Name");

        Author author = mock(Author.class);
        when(author.getVersion()).thenReturn(1L);

        when(authorService.partialUpdate(eq("AUTH123"), any(UpdateAuthorRequest.class), anyLong())).thenReturn(author);
        when(authorViewMapper.toAuthorView(author)).thenReturn(new AuthorView());
        when(concurrencyService.getVersionFromIfMatchHeader(anyString())).thenReturn(1L);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/authors/AUTH123")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Name\"}")
                        .header("If-Match", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindByAuthorNumber() throws Exception {
        Author author = mock(Author.class);
        when(author.getVersion()).thenReturn(1L);

        when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.of(author));
        when(authorViewMapper.toAuthorView(author)).thenReturn(new AuthorView());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/AUTH123")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"1\""));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testFindByName() throws Exception {
        List<Author> authors = List.of(mock(Author.class));
        when(authorService.findByName("Author Name")).thenReturn(authors);
        when(authorViewMapper.toAuthorView(authors)).thenReturn(List.of(new AuthorView()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("name", "Author Name"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetBooksByAuthorNumber() throws Exception {
        List<BookView> bookViews = List.of(new BookView());

        when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.of(mock(Author.class)));
        when(authorService.findBooksByAuthorNumber("AUTH123")).thenReturn(List.of(mock(Book.class)));
        when(bookViewMapper.toBookView(anyList())).thenReturn(bookViews);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/AUTH123/books")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetTop5Authors() throws Exception {
        List<AuthorLendingView> topAuthors = List.of(new AuthorLendingView());
        when(authorService.findTopAuthorByLendings()).thenReturn(topAuthors);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/top5")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAuthorPhoto() throws Exception {
        Author author = mock(Author.class);
        when(author.getPhoto()).thenReturn(mock(Photo.class));
        when(author.getPhoto().getPhotoFile()).thenReturn("photo.jpg");

        when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.of(author));
        when(fileStorageService.getFile("photo.jpg")).thenReturn(new byte[]{1, 2, 3});
        when(fileStorageService.getExtension("photo.jpg")).thenReturn(Optional.of("jpeg"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/AUTH123/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetAuthorPhotoNoPhoto() throws Exception {
        Author author = mock(Author.class);

        when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.of(author));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/AUTH123/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[0]));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testGetCoAuthorsWithBooks() throws Exception {
        Author author = mock(Author.class);
        AuthorView authorView = mock(AuthorView.class);
        CoAuthorView coAuthorView = mock(CoAuthorView.class);

        List<Author> coAuthors = List.of(author);

        List<CoAuthorView> coAuthorViews = List.of(coAuthorView);



        when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.of(author));
        when(authorService.findCoAuthorsByAuthorNumber("AUTH123")).thenReturn(coAuthors);
        when(authorViewMapper.toAuthorCoAuthorBooksView(any(Author.class), anyList())).thenReturn(new AuthorCoAuthorBooksView(authorView, coAuthorViews));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/AUTH123/coauthors")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    // @Test
    // @WithMockUser(username = "testuser", roles = {"USER"})
    // void testDeleteAuthorPhoto() throws Exception {
    //     Author author = mock(Author.class);
    //     when(author.getAuthorNumber()).thenReturn("AUTH123");
    //     when(author.getVersion()).thenReturn(1L);
    //     Photo photo = mock(Photo.class);
    //     when(author.getPhoto()).thenReturn(photo);
    //     when(photo.getPhotoFile()).thenReturn("photo.jpg");

    //     when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.of(author));

    //     mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/AUTH123/photo")
    //                     .with(SecurityMockMvcRequestPostProcessors.csrf()))
    //                         .andExpect(status().isOk());

    //     verify(fileStorageService).deleteFile("photo.jpg");
    //     verify(authorService).removeAuthorPhoto("AUTH123", author.getVersion());
    // }

    // @Test
    // @WithMockUser(username = "testuser", roles = {"USER"})
    // void testDeleteAuthorPhotoNoPhoto() throws Exception {
    //     Author author = mock(Author.class);
    //     when(author.getAuthorNumber()).thenReturn("AUTH123");
    //     when(author.getPhoto()).thenReturn(null);

    //     when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.of(author));

    //     mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/AUTH123/photo")
    //                     .with(SecurityMockMvcRequestPostProcessors.csrf()))
    //                         .andExpect(status().isNotFound());

    //     verify(fileStorageService, never()).deleteFile(anyString());
    //     verify(authorService, never()).removeAuthorPhoto(anyString(), anyLong());
    // }

    // @Test
    // @WithMockUser(username = "testuser", roles = {"USER"})
    // void testDeleteAuthorInvalidAuthorNumber() throws Exception {
    //     when(authorService.findByAuthorNumber("AUTH123")).thenReturn(Optional.empty());

    //     mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/AUTH123/photo")
    //                     .with(SecurityMockMvcRequestPostProcessors.csrf()))
    //                         .andExpect(status().isForbidden());

    //     verify(fileStorageService, never()).deleteFile(anyString());
    //     verify(authorService, never()).removeAuthorPhoto(anyString(), anyLong());
    // }
}
