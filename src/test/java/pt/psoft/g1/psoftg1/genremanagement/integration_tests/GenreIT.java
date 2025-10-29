//package pt.psoft.g1.psoftg1.genremanagement.integration_tests;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
//
///* Integration test opaque-box do GenreController */
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//public class GenreIT
//{
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private GenreRepository genreRepository;
//
//    @BeforeEach
//    public void setUp()
//    {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
//    @Rollback
//    public void getAverageLendings() throws Exception
//    {
//
//    }
//
//}
