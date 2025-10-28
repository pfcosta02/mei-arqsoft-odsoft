package pt.psoft.g1.psoftg1.usermanagement.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserMapperMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb.SpringDataUserRepositoryMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb.UserRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.LibrarianMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.services.SearchUsersQuery;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserRepositoryMongoDBImplTest {
    @InjectMocks
    private UserRepositoryMongoDBImpl userRepository;

    @Mock
    private SpringDataUserRepositoryMongoDB mongoRepository;

    @Mock
    private UserMapperMongoDB userEntityMapper;

    @Mock
    private EntityManager em;

    @Mock
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(userRepository, "mongoTemplate", mongoTemplate);
    }

    // ------------------- FINDERS -------------------

    @Test
    void testFindByIdFound() {
        UserMongoDB mockEntity = mock(UserMongoDB.class);
        User mockUser = mock(User.class);

        when(mongoRepository.findById("1L")).thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toModel(mockEntity)).thenReturn(mockUser);

        Optional<User> result = userRepository.findById("1L");

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        when(mongoRepository.findById("1L")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findById("1L");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUsernameFound() {
        UserMongoDB mockEntity = mock(UserMongoDB.class);
        User mockUser = mock(User.class);

        when(mongoRepository.findByUsername("user")).thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toModel(mockEntity)).thenReturn(mockUser);

        Optional<User> result = userRepository.findByUsername("user");

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(mongoRepository.findByUsername("user")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByUsername("user");

        assertTrue(result.isEmpty());
    }

    // ------------------- SAVE -------------------

    @Test
    void testSaveReader() {
        Reader reader = mock(Reader.class);
        ReaderMongoDB readerEntity = mock(ReaderMongoDB.class);
        ReaderMongoDB savedEntity = mock(ReaderMongoDB.class);
        Reader mappedReader = mock(Reader.class);

        when(userEntityMapper.toEntity(reader)).thenReturn(readerEntity);
        when(mongoRepository.save(readerEntity)).thenReturn(savedEntity);
        when(userEntityMapper.toModel(savedEntity)).thenReturn(mappedReader);

        User result = userRepository.save(reader);

        assertEquals(mappedReader, result);
    }

    @Test
    void testSaveLibrarian() {
        Librarian librarian = mock(Librarian.class);
        LibrarianMongoDB librarianEntity = mock(LibrarianMongoDB.class);
        LibrarianMongoDB savedEntity = mock(LibrarianMongoDB.class);
        Librarian mappedLibrarian = mock(Librarian.class);

        when(userEntityMapper.toEntity(librarian)).thenReturn(librarianEntity);
        when(mongoRepository.save(librarianEntity)).thenReturn(savedEntity);
        when(userEntityMapper.toModel(savedEntity)).thenReturn(mappedLibrarian);

        User result = userRepository.save(librarian);

        assertEquals(mappedLibrarian, result);
    }

    @Test
    void testSaveGenericUser() {
        User user = mock(User.class);
        UserMongoDB userEntity = mock(UserMongoDB.class);
        UserMongoDB savedEntity = mock(UserMongoDB.class);
        User mappedUser = mock(User.class);

        when(userEntityMapper.toEntity(user)).thenReturn(userEntity);
        when(mongoRepository.save(userEntity)).thenReturn(savedEntity);
        when(userEntityMapper.toModel(savedEntity)).thenReturn(mappedUser);

        User result = userRepository.save(user);

        assertEquals(mappedUser, result);
    }
    // ------------------- SEARCH -------------------

    @Test
    void testSearchUsersWithEmptyFields() {
        SearchUsersQuery query = mock(SearchUsersQuery.class);
        Page page = mock(Page.class);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<UserMongoDB> cq = mock(CriteriaQuery.class);
        Root<UserMongoDB> root = mock(Root.class);
        TypedQuery<UserMongoDB> typedQuery = mock(TypedQuery.class);

        when(query.getUsername()).thenReturn("");
        when(query.getFullName()).thenReturn("");
        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(UserMongoDB.class)).thenReturn(cq);
        when(cq.from(UserMongoDB.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<User> result = userRepository.searchUsers(page, query);

        assertTrue(result.isEmpty());
    }

    // ------------------- NAME SEARCH -------------------

    @Test
    void testFindByNameName() {
        UserMongoDB entity = mock(UserMongoDB.class);
        User user = mock(User.class);

        when(mongoRepository.findByNameName("Ana")).thenReturn(List.of(entity));
        when(userEntityMapper.toModel(entity)).thenReturn(user);

        List<User> result = userRepository.findByNameName("Ana");

        assertEquals(1, result.size());
        assertTrue(result.contains(user));
    }

    @Test
    void testFindByNameNameContains() {
        UserMongoDB entity = mock(UserMongoDB.class);
        User user = mock(User.class);

        when(mongoRepository.findByNameNameContains("Ana")).thenReturn(List.of(entity));
        when(userEntityMapper.toModel(entity)).thenReturn(user);

        List<User> result = userRepository.findByNameNameContains("Ana");

        assertEquals(1, result.size());
        assertTrue(result.contains(user));
    }
}
