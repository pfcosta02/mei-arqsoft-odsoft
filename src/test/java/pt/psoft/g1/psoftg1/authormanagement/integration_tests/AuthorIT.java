package pt.psoft.g1.psoftg1.authormanagement.integration_tests;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorMapper;
import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.Description;
import pt.psoft.g1.psoftg1.bookmanagement.model.Isbn;
import pt.psoft.g1.psoftg1.bookmanagement.model.Title;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.idgeneratormanagement.infrastructure.IdGenerator;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import pt.psoft.g1.psoftg1.shared.model.Photo;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;

/* Integration test opaque-box do Controller + Service + Domain */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthorIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LendingRepository lendingRepository;
    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private AuthorMapper mapper;

    @MockBean
    private PhotoRepository photoRepository;


    @MockBean
    private IdGenerator idGenerator;

    /*
     * Este teste faz o seguinte:
     * 1- verifica que o Author nao existe;
     * 2- cria o Author;
     * 3- verifica que o Author foi criado;
     */
    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnNewAuthorAndOk() throws Exception
    {
        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "fake-image-content".getBytes());
        when(fileStorageService.getRequestPhoto(photo)).thenReturn(null);

        Author author = new Author("Pedro", "Sinceramente nao sei", null);
        author.setAuthorNumber("1000");
        when(mapper.create(any(CreateAuthorRequest.class))).thenReturn(author);

        when(authorRepository.save(any())).thenReturn(author);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/authors")
                        .param("name", "Pedro")
                        .param("bio", "Sinceramente nao sei")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.bio").value("Sinceramente nao sei"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldUpdateAuthorWithIfMatch() throws Exception
    {

        MockMultipartFile photo = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", "fake-image-content".getBytes());
        when(fileStorageService.getRequestPhoto(photo)).thenReturn(null);

        Author author = new Author("Pedro", "Sinceramente nao sei", null);
        author.setAuthorNumber("1000");
        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));

        Author newAuthor = new Author("Pedro", "Nova descricao", null);
        newAuthor.setAuthorNumber("1000");
        when(authorRepository.save(author)).thenReturn(newAuthor);

        // Atualizar parcial via PATCH
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/authors/" + 1000)
                        .param("bio", "Nova descricao")
                        .header("If-Match", "0")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnAuthorByAuthorNumber() throws Exception {
        Author author = new Author("Pedro", "Bio do Pedro", null);
        author.setAuthorNumber("1000");

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/1000")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"0\""))
                .andExpect(jsonPath("$.authorNumber").value("1000"))
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.bio").value("Bio do Pedro"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnAuthorsByName() throws Exception {
        Author author = new Author("Pedro", "Bio do Pedro", null);
        author.setAuthorNumber("1000");

        when(authorRepository.searchByNameNameStartsWith("Pedro")).thenReturn(List.of(author));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors")
                        .param("name", "Pedro")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].authorNumber").value("1000"))
                .andExpect(jsonPath("$.items[0].name").value("Pedro"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READER"})
    void shouldReturnBooksByAuthorNumber() throws Exception {
        Author author = new Author("Pedro", "Bio", null);
        author.setAuthorNumber("1000");

        Isbn isbn = new Isbn("9789722328296");
        Title title = new Title("Novo Título");
        Description description = new Description("Nova descrição");
        Genre genre = new Genre("Terror");
        Book book = new Book(isbn.getIsbn(), title.getTitle(), description.getDescription(), genre, List.of(author), null);
        book.setBookId("1");

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));
        when(bookRepository.findBooksByAuthorNumber("1000")).thenReturn(List.of(book));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/1000/books")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].isbn").value("9789722328296"))
                .andExpect(jsonPath("$.items[0].title").value("Novo Título"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READER"})
    void test_invalidAuthorNumber() throws Exception {
        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.empty());


        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/1000/books")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnAuthorPhoto() throws Exception {
        // Arrange
        Author author = new Author("Pedro", "Bio", null);
        author.setAuthorNumber("1000");

        Photo photo = new Photo(Path.of("photo.jpg"));
        author.setPhoto(photo.getPhotoFile());

        byte[] fakeImage = "fake-image-content".getBytes();

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));
        when(fileStorageService.getFile("photo.jpg")).thenReturn(fakeImage);
        when(fileStorageService.getExtension("photo.jpg")).thenReturn(Optional.of("jpg"));

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/1000/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(fakeImage));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnOkWhenAuthorHasNoPhoto() throws Exception {
        Author author = new Author("Pedro", "Bio", null);
        author.setAuthorNumber("1000");
        author.setPhoto(null); // Sem foto

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/1000/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("")); // corpo vazio
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnBadRequestWhenExtensionIsMissing() throws Exception {
        Author author = new Author("Pedro", "Bio", null);
        author.setAuthorNumber("1000");

        Photo photo = new Photo(Path.of("photo.unknown"));
        author.setPhoto(photo.getPhotoFile());

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));
        when(fileStorageService.getFile("photo.unknown")).thenReturn("fake".getBytes());
        when(fileStorageService.getExtension("photo.unknown")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/1000/photo"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"READER"})
    void shouldReturnCoAuthorsWithBooks() throws Exception {
        // Autor principal
        Author mainAuthor = new Author("Pedro", "Bio", null);
        mainAuthor.setAuthorNumber("1000");

        // Coautor
        Author coAuthor = new Author("Joana", "Coautora", null);
        coAuthor.setAuthorNumber("2000");

        // Livro do coautor
        Isbn isbn = new Isbn("9789722328296");
        Title title = new Title("Livro Coautoria");
        Description description = new Description("Descrição");
        Genre genre = new Genre("Terror");
        Book book = new Book(isbn.getIsbn(), title.getTitle(), description.getDescription(), genre, List.of(coAuthor), null);
        book.setBookId("1");

        // Mock do repositório
        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(mainAuthor));
        when(authorRepository.findCoAuthorsByAuthorNumber("1000")).thenReturn(List.of(coAuthor));
        when(bookRepository.findBooksByAuthorNumber("2000")).thenReturn(List.of(book));

        // Execução do teste
        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/1000/coauthors")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author.authorNumber").value("1000"))
                .andExpect(jsonPath("$.coauthors[0].name").value("Joana"))
                .andExpect(jsonPath("$.coauthors[0].books[0].isbn").value(isbn.getIsbn()));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldDeleteAuthorPhotoSuccessfully() throws Exception {
        // Arrange
        Author author = new Author("Pedro", "Bio", null);
        author.setAuthorNumber("1000");
        Photo photo = new Photo(Path.of("photo.jpg"));
        author.setPhoto(photo.getPhotoFile());

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));
        when(authorRepository.save(any())).thenReturn(author);

        // Act + Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/1000/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnForbiddenWhenAuthorNotFoundOnDeletePhoto() throws Exception {
        when(authorRepository.findByAuthorNumber("9999")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/9999/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"LIBRARIAN"})
    void shouldReturnNotFoundWhenAuthorHasNoPhoto() throws Exception {
        Author author = new Author("Pedro", "Bio", null);
        author.setAuthorNumber("1000");
        author.setPhoto(null);

        when(authorRepository.findByAuthorNumber("1000")).thenReturn(Optional.of(author));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/1000/photo")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }
}