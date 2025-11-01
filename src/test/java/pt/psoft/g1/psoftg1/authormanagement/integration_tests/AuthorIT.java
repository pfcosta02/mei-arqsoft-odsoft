//package pt.psoft.g1.psoftg1.authormanagement.integration_tests;
//
//import static org.junit.Assert.assertNull;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import jakarta.transaction.Transactional;
//import pt.psoft.g1.psoftg1.authormanagement.api.AuthorView;
//import pt.psoft.g1.psoftg1.authormanagement.model.Author;
//import pt.psoft.g1.psoftg1.authormanagement.model.Bio;
//import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
//import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;
//import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
//import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
//import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
//import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
//import pt.psoft.g1.psoftg1.idgeneratormanagement.infrastructure.IdGenerator;
//import pt.psoft.g1.psoftg1.shared.model.Name;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//import org.apache.commons.lang3.RandomStringUtils;
//
///* Integration test opaque-box do AuthorController */
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional //Vai limpar a BD no final de cada teste
//public class AuthorIT {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private BookRepository bookRepository;
//
//    @Autowired
//    private GenreRepository genreRepository;
//
//    @Autowired
//    private AuthorRepository authorRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private IdGenerator idGenerator;
//
//    @BeforeEach
//    public void setUp()
//    {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    /*
//     * Este teste faz o seguinte:
//     * 1- verifica que o Author nao existe;
//     * 2- cria o Author;
//     * 3- verifica que o Author foi criado;
//     */
//    @Test
//    @WithMockUser(username = "maria@gmail.com", roles = {"LIBRARIAN"})
//    public void shouldReturnNewAuthorAndOk() throws Exception
//    {
//        String generatedName = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio = RandomStringUtils.randomAlphabetic(50);
//        CreateAuthorRequest newAuthorDTO = new CreateAuthorRequest();
//        newAuthorDTO.setName(generatedName);
//        newAuthorDTO.setBio(generatedBio);
//
//        // First call: GET /api/authors?name={authorName}
//        // Verificar que nao existe nenhum autor com o nome gerado e, portanto, retorna uma lista vazia
//        // Nao faco nenhum assert porque ja verifico no pedido que ele vem vazio
//        mockMvc
//            .perform(MockMvcRequestBuilders.get("/api/authors?name=" + generatedName)
//            .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.items").isArray())
//            .andExpect(jsonPath("$.items").isEmpty())
//            .andReturn();
//
//
//        // Second call: POST [{"name":?generatedName?, "bio":?generatedBio?}]
//        MvcResult result2 = mockMvc
//                                .perform(MockMvcRequestBuilders.multipart("/api/authors")
//                                    .param("name", generatedName)
//                                    .param("bio", generatedBio))
//                                .andExpect(status().isCreated())
//                                .andReturn();
//
//
//        String resultContent = result2.getResponse().getContentAsString();
//        assertNotNull(resultContent);
//
//        AuthorView response = objectMapper.readValue(resultContent, AuthorView.class);
//        assertEquals(generatedName, response.getName());
//        assertEquals(generatedBio, response.getBio());
//        assertNull(response.getPhoto());
//
//        String authorNumber = response.getAuthorNumber();
//
//        // Third call: GET /api/authors/{authorNumber}
//        // Testar outro endpoint
//        MvcResult result3 = mockMvc
//                                .perform(MockMvcRequestBuilders.get("/api/authors/" + authorNumber)
//                                .accept(MediaType.APPLICATION_JSON))
//                                .andExpect(status().isOk())
//                                .andReturn();
//        String resultContent3 = result3.getResponse().getContentAsString();
//        assertNotNull(resultContent3);
//
//        AuthorView response2 = objectMapper.readValue(resultContent3, AuthorView.class);
//        assertEquals(generatedName, response2.getName());
//        assertEquals(generatedBio, response2.getBio());
//    }
//
//    @Test
//    @WithMockUser(username = "maria@gmail.com", roles = {"LIBRARIAN"})
//    public void shouldUpdateAuthorWithIfMatch() throws Exception
//    {
//        String generatedName = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio = RandomStringUtils.randomAlphabetic(50);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/authors")
//                .param("name", generatedName)
//                .param("bio", generatedBio))
//                .andExpect(status().isCreated())
//                .andReturn();
//
//        AuthorView author = objectMapper.readValue(result.getResponse().getContentAsString(), AuthorView.class);
//        String authorNumber = author.getAuthorNumber();
//        String eTag = result.getResponse().getHeader("ETag");
//
//        // Atualizar parcial via PATCH
//        String updatedBio = RandomStringUtils.randomAlphabetic(30);
//        MvcResult patchResult = mockMvc
//                                    .perform(MockMvcRequestBuilders.patch("/api/authors/" + authorNumber)
//                                    .param("bio", updatedBio)
//                                    .header("If-Match", eTag)
//                                    .contentType(MediaType.MULTIPART_FORM_DATA))
//                                    .andExpect(status().isOk())
//                                    .andReturn();
//
//        AuthorView updatedAuthor = objectMapper.readValue(patchResult.getResponse().getContentAsString(), AuthorView.class);
//        assertEquals(updatedBio, updatedAuthor.getBio());
//        assertEquals(generatedName, updatedAuthor.getName());
//    }
//
//    @Test
//    @WithMockUser(username = "maria@gmail.com", roles = {"LIBRARIAN"})
//    public void shouldReturnBadRequestWithoutIfMatch() throws Exception
//    {
//        String generatedName = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio = RandomStringUtils.randomAlphabetic(50);
//
//        MvcResult result = mockMvc
//                            .perform(MockMvcRequestBuilders.multipart("/api/authors")
//                            .param("name", generatedName)
//                            .param("bio", generatedBio))
//                            .andExpect(status().isCreated())
//                            .andReturn();
//
//        AuthorView author = objectMapper.readValue(result.getResponse().getContentAsString(), AuthorView.class);
//        String authorNumber = author.getAuthorNumber();
//
//        // PATCH sem If-Match
//        mockMvc
//            .perform(MockMvcRequestBuilders.patch("/api/authors/" + authorNumber)
//            .param("bio", "updated bio")
//            .contentType(MediaType.MULTIPART_FORM_DATA))
//            .andExpect(status().isBadRequest());
//    }
//
//    // Aqui temos o problema de nao existir books
//    @Test
//    @WithMockUser(username = "manuel@gmail.com", roles = {"READER"})
//    @Rollback
//    public void shouldReturnOkWhenGettingAuthorBooks() throws Exception
//    {
//        String generatedName = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio = RandomStringUtils.randomAlphabetic(50);
//        String generatedDescription = RandomStringUtils.randomAlphabetic(60);
//
//        // Create a new Genre:
//        Genre genreToSave = new Genre("Programming");
//        genreToSave.setPk(idGenerator.generateId());
//
//        Genre genre = genreRepository.save(genreToSave);
//
//        // Create a new Author:
//        Author authorModel = authorRepository.save(new Author(new Name(generatedName), new Bio(generatedBio), null));
//        List<Author> authors = new ArrayList<>();
//        authors.add(authorModel);
//
//        String isbn = "9789720706386";
//
//        // Create a new Book:
//        Book bookToSave = new Book(isbn, "Clean Code", generatedDescription, genre, authors, null);
//        bookToSave.setBookId(idGenerator.generateId());
//
//        bookRepository.save(bookToSave);
//
//        // GET livros do autor
//        mockMvc
//            .perform(MockMvcRequestBuilders.get("/api/authors/" + authorModel.getAuthorNumber() + "/books")
//            .accept(MediaType.APPLICATION_JSON))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.items").isArray());
//    }
//
//    @Test
//    @WithMockUser(username = "maria@gmail.com", roles = {"LIBRARIAN"})
//    public void shouldReturnOkWhenGettingAuthorPhotoWithoutPhoto() throws Exception
//    {
//        String generatedName = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio = RandomStringUtils.randomAlphabetic(50);
//
//        MvcResult result = mockMvc
//                                .perform(MockMvcRequestBuilders.multipart("/api/authors")
//                                .param("name", generatedName)
//                                .param("bio", generatedBio))
//                                .andExpect(status().isCreated())
//                                .andReturn();
//
//        AuthorView author = objectMapper.readValue(result.getResponse().getContentAsString(), AuthorView.class);
//        String authorNumber = author.getAuthorNumber();
//
//        // GET foto (autor sem foto)
//        MvcResult result2 = mockMvc
//                                .perform(MockMvcRequestBuilders.get("/api/authors/" + authorNumber + "/photo"))
//                                .andExpect(status().isOk())
//                                .andReturn();
//
//        assertTrue(result2.getResponse().getContentAsByteArray().length == 0);
//    }
//
//    @Test
//    @WithMockUser(username = "maria@gmail.com", roles = {"LIBRARIAN"})
//    public void shouldReturnNotFoundWhenDeletingPhotoThatDoesNotExist() throws Exception
//    {
//        String generatedName = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio = RandomStringUtils.randomAlphabetic(50);
//
//        MvcResult result = mockMvc
//                                .perform(MockMvcRequestBuilders.multipart("/api/authors")
//                                .param("name", generatedName)
//                                .param("bio", generatedBio))
//                                .andExpect(status().isCreated())
//                                .andReturn();
//
//        AuthorView author = objectMapper.readValue(result.getResponse().getContentAsString(), AuthorView.class);
//        String authorNumber = author.getAuthorNumber();
//
//        // DELETE foto inexistente
//        mockMvc
//            .perform(MockMvcRequestBuilders.delete("/api/authors/" + authorNumber + "/photo"))
//            .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = "maria@gmail.com", roles = {"LIBRARIAN"})
//    public void shouldReturnNotFoundWhenGettingTop5WithNoAuthors() throws Exception
//    {
//        // Assumindo BD limpo (sem autores)
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/authors/top5"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    @WithMockUser(username = "manuel@gmail.com", roles = {"READER"})
//    public void shouldReturnOkWhenGettingCoauthorsWithoutAny() throws Exception
//    {
//        String generatedName = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio = RandomStringUtils.randomAlphabetic(50);
//        String generatedDescription = RandomStringUtils.randomAlphabetic(60);
//
//        String generatedName2 = RandomStringUtils.randomAlphabetic(10);
//        String generatedBio2 = RandomStringUtils.randomAlphabetic(50);
//
//        // Create a new Genre:
//        Genre genreToSave = new Genre("Programming");
//        genreToSave.setPk(idGenerator.generateId());
//
//        Genre genre = genreRepository.save(genreToSave);
//
//        // Create a new Author:
//        Author authorModel = authorRepository.save(new Author(new Name(generatedName), new Bio(generatedBio), null));
//
//        // Create a authro to be coauthor
//        Author authorModel2 = authorRepository.save(new Author(new Name(generatedName2), new Bio(generatedBio2), null));
//        List<Author> authors = new ArrayList<>();
//        authors.add(authorModel);
//        authors.add(authorModel2);
//
//        String isbn = "9789720706386";
//
//        // Create a new Book:
//        Book bookToSave = new Book(isbn, "Clean Code", generatedDescription, genre, authors, null);
//        bookToSave.setBookId(idGenerator.generateId());
//
//        bookRepository.save(bookToSave);
//
//        // GET coautores
//        mockMvc
//            .perform(MockMvcRequestBuilders.get("/api/authors/" + authorModel.getAuthorNumber() + "/coauthors"))
//            .andExpect(status().isOk())
//            .andExpect(jsonPath("$.author").exists())
//            .andExpect(jsonPath("$.coauthors").exists())
//            .andExpect(jsonPath("$.coauthors[?(@.name=='" + authorModel2.getName().getName() + "')]").exists());
//    }
//}