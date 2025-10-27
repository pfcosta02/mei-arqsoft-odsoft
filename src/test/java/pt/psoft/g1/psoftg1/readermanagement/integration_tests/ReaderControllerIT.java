package pt.psoft.g1.psoftg1.readermanagement.integration_tests;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;


/* Integration test opaque-box do BookController */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ReaderControllerIT 
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReaderRepository readerRepo;

    @BeforeEach
    public void setUp() 
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"LIBRARIAN"})
    @Rollback
    public void findByReaderNumber() throws Exception 
    {
    //    Reader reader = new Reader(User, null)
    //    readerRepo.save(null)
    }
}
