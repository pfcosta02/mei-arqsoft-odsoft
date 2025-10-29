package pt.psoft.g1.psoftg1.usermanagement.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational.SpringDataUserRepository;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational.UserRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.LibrarianEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;
import pt.psoft.g1.psoftg1.usermanagement.services.SearchUsersQuery;

class UserRepositoryRelationalImplTest {

    @InjectMocks
    private UserRepositoryRelationalImpl userRepository;

    @Mock
    private SpringDataUserRepository sqlRepo;

    @Mock
    private UserEntityMapper userEntityMapper;

    @Mock
    private EntityManager em;

    @BeforeEach
    void setUp() 
    {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- FINDERS -------------------

    @Test
    void testFindByIdFound() {
        UserEntity mockEntity = mock(UserEntity.class);
        User mockUser = mock(User.class);

        when(sqlRepo.findById("1L")).thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toModel(mockEntity)).thenReturn(mockUser);

        Optional<User> result = userRepository.findById("1L");

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        when(sqlRepo.findById("1L")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findById("1L");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByUsernameFound() {
        UserEntity mockEntity = mock(UserEntity.class);
        User mockUser = mock(User.class);

        when(sqlRepo.findByUsername("user")).thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toModel(mockEntity)).thenReturn(mockUser);

        Optional<User> result = userRepository.findByUsername("user");

        assertTrue(result.isPresent());
        assertEquals(mockUser, result.get());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(sqlRepo.findByUsername("user")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByUsername("user");

        assertTrue(result.isEmpty());
    }

    // ------------------- SAVE -------------------

    @Test
    void testSaveReader() {
        Reader reader = mock(Reader.class);
        ReaderEntity readerEntity = mock(ReaderEntity.class);
        ReaderEntity savedEntity = mock(ReaderEntity.class);
        Reader mappedReader = mock(Reader.class);

        when(userEntityMapper.toEntity(reader)).thenReturn(readerEntity);
        when(sqlRepo.save(readerEntity)).thenReturn(savedEntity);
        when(userEntityMapper.toModel(savedEntity)).thenReturn(mappedReader);

        User result = userRepository.save(reader);

        assertEquals(mappedReader, result);
    }

    @Test
    void testSaveLibrarian() {
        Librarian librarian = mock(Librarian.class);
        LibrarianEntity librarianEntity = mock(LibrarianEntity.class);
        LibrarianEntity savedEntity = mock(LibrarianEntity.class);
        Librarian mappedLibrarian = mock(Librarian.class);

        when(userEntityMapper.toEntity(librarian)).thenReturn(librarianEntity);
        when(sqlRepo.save(librarianEntity)).thenReturn(savedEntity);
        when(userEntityMapper.toModel(savedEntity)).thenReturn(mappedLibrarian);

        User result = userRepository.save(librarian);

        assertEquals(mappedLibrarian, result);
    }

    @Test
    void testSaveGenericUser() {
        User user = mock(User.class);
        UserEntity userEntity = mock(UserEntity.class);
        UserEntity savedEntity = mock(UserEntity.class);
        User mappedUser = mock(User.class);

        when(userEntityMapper.toEntity(user)).thenReturn(userEntity);
        when(sqlRepo.save(userEntity)).thenReturn(savedEntity);
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
        CriteriaQuery<UserEntity> cq = mock(CriteriaQuery.class);
        Root<UserEntity> root = mock(Root.class);
        TypedQuery<UserEntity> typedQuery = mock(TypedQuery.class);

        when(query.getUsername()).thenReturn("");
        when(query.getFullName()).thenReturn("");
        when(page.getNumber()).thenReturn(1);
        when(page.getLimit()).thenReturn(10);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(UserEntity.class)).thenReturn(cq);
        when(cq.from(UserEntity.class)).thenReturn(root);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        List<User> result = userRepository.searchUsers(page, query);

        assertTrue(result.isEmpty());
    }

    // ------------------- NAME SEARCH -------------------

    @Test
    void testFindByNameName() {
        UserEntity entity = mock(UserEntity.class);
        User user = mock(User.class);

        when(sqlRepo.findByNameName("Ana")).thenReturn(List.of(entity));
        when(userEntityMapper.toModel(entity)).thenReturn(user);

        List<User> result = userRepository.findByNameName("Ana");

        assertEquals(1, result.size());
        assertTrue(result.contains(user));
    }

    @Test
    void testFindByNameNameContains() {
        UserEntity entity = mock(UserEntity.class);
        User user = mock(User.class);

        when(sqlRepo.findByNameNameContains("Ana")).thenReturn(List.of(entity));
        when(userEntityMapper.toModel(entity)).thenReturn(user);

        List<User> result = userRepository.findByNameNameContains("Ana");

        assertEquals(1, result.size());
        assertTrue(result.contains(user));
    }
}
