package pt.psoft.g1.psoftg1.authormanagement.repository;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.redis.AuthorRepositoryRedisImpl;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.AuthorRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.SpringDataAuthorRepository;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.redis.BookRepositoryRedisImpl;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;

import javax.swing.text.html.Option;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/* Teste Unitario, opaque-box do AuthorRepositoryImpl */
class AuthorRepositoryRelationalImplTest
{
    @InjectMocks
    private AuthorRepositoryRelationalImpl authoRepo;

    @Mock
    private SpringDataAuthorRepository sqlRepo;

    @Mock
    private AuthorRepositoryRedisImpl redisRepo;

    @Mock
    private AuthorEntityMapper authorEntityMapper;

    @BeforeEach
    void setUp() 
    {
        MockitoAnnotations.openMocks(this);
    }

    // ------------------- GETTERS -------------------
    @org.junit.jupiter.api.Test
    void testFindByAuthorNumber()
    {
        // Arrange
        Author mockAuthor = mock(Author.class);
        Optional<Author> mockOptAuthor = Optional.of(mockAuthor);

        AuthorEntity authorEntity = mock(AuthorEntity.class);
        when(redisRepo.getAuthorFromRedis(anyString())).thenReturn(mockOptAuthor);
        when(sqlRepo.findByAuthorNumber(anyString())).thenReturn(Optional.of(authorEntity));
        when(authorEntityMapper.toModel(authorEntity)).thenReturn(mockAuthor);

        // Act
        Optional<Author> aut = authoRepo.findByAuthorNumber(anyString());
        doNothing().when(redisRepo).save(mockAuthor);

        // Assert
        assertEquals(mockAuthor, aut.get());
    }

    @org.junit.jupiter.api.Test
    void testFindByInvalidAuthorNumber()
    {
        // Arrange
        when(redisRepo.getAuthorFromRedis(anyString())).thenReturn(Optional.empty());
        when(sqlRepo.findByAuthorNumber(anyString())).thenReturn( Optional.empty());

        // Act
        Optional<Author> autRedis = redisRepo.getAuthorFromRedis(anyString());
        Optional<Author> aut = authoRepo.findByAuthorNumber(anyString());

        // Assert
        assertEquals(Optional.empty(), autRedis);
        assertEquals(Optional.empty(), aut);
    }

    @org.junit.jupiter.api.Test
    void testSearchByNameNameStartsWith()
    {
        // Arrange
        Author mockAuthorCache = mock(Author.class);
        List<Author> cached = new ArrayList<>();
        cached.add(mockAuthorCache);

        List<AuthorEntity> list = new ArrayList<>();
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        list.add(mockAuthorEntity);

        when(redisRepo.getAuthorListFromRedis(anyString())).thenReturn(cached);
        when(sqlRepo.searchByNameNameStartsWith(anyString())).thenReturn(list);

        Author mockAuthor = mock(Author.class);
        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);

        // Act
        List<Author> aut = authoRepo.searchByNameNameStartsWith(anyString());
        doNothing().when(redisRepo).cacheAuthorListToRedis(anyString(), eq(aut));

        // Assert
        assertEquals(list.size(), aut.size());
    }

    @org.junit.jupiter.api.Test
    void testSearchByInvalidNameNameStartsWith()
    {
        // Arrange
        List<Author> list_ = new ArrayList<>();
        List<AuthorEntity> list = new ArrayList<>();

        when(redisRepo.getAuthorListFromRedis(anyString())).thenReturn(list_);
        when(sqlRepo.searchByNameNameStartsWith(anyString())).thenReturn(list);

        // Act
        List<Author> aut = authoRepo.searchByNameNameStartsWith(anyString());
        doNothing().when(redisRepo).cacheAuthorListToRedis(anyString(), eq(aut));

        // Assert
        assertEquals(list.size(), aut.size());
    }

    @org.junit.jupiter.api.Test
    void testSearchByName()
    {
        // Arrange
        Author mockAuthorCache = mock(Author.class);
        List<Author> cached = new ArrayList<>();
        cached.add(mockAuthorCache);

        List<AuthorEntity> list = new ArrayList<>();
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        list.add(mockAuthorEntity);

        when(redisRepo.getAuthorListFromRedis(anyString())).thenReturn(cached);
        when(sqlRepo.searchByNameName(anyString())).thenReturn(list);

        Author mockAuthor = mock(Author.class);
        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);

        // Act
        List<Author> aut = authoRepo.searchByNameName(anyString());
        List<Author> autcache = redisRepo.getAuthorListFromRedis(anyString());
        doNothing().when(redisRepo).cacheAuthorListToRedis(anyString(), eq(autcache));

        // Assert
        assertEquals(list.size(), aut.size());
        assertEquals(cached.size(), autcache.size());
    }

    @org.junit.jupiter.api.Test
    void testSearchByInvalidName()
    {
        // Arrange
        List<Author> cached = new ArrayList<>();
        List<AuthorEntity> list = new ArrayList<>();

        when(redisRepo.getAuthorListFromRedis(anyString())).thenReturn(cached);
        when(sqlRepo.searchByNameName(anyString())).thenReturn(list);

        // Act
        List<Author> aut = authoRepo.searchByNameName(anyString());
        List<Author> autcache = redisRepo.getAuthorListFromRedis(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
        assertEquals(cached.size(), autcache.size());
    }

    @org.junit.jupiter.api.Test
    void testFindAll()
    {
        // Arrange
        List<AuthorEntity> list = new ArrayList<>();
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        list.add(mockAuthorEntity);

        when(sqlRepo.findAll()).thenReturn(list);

        Author mockAuthor = mock(Author.class);
        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);

        // Act
        Iterable<Author> aut = authoRepo.findAll();

        // Assert
        List<Author> resultList = StreamSupport
            .stream(aut.spliterator(), false)
            .collect(Collectors.toList());

        assertEquals(list.size(), resultList.size());
        assertTrue(resultList.contains(mockAuthor));
    }

    @org.junit.jupiter.api.Test
    void testFindAllEmpty()
    {
        // Arrange
        List<AuthorEntity> list = new ArrayList<>();

        when(sqlRepo.findAll()).thenReturn(list);

        // Act
        Iterable<Author> aut = authoRepo.findAll();

        // Assert
        List<Author> resultList = StreamSupport
            .stream(aut.spliterator(), false)
            .collect(Collectors.toList());

        assertEquals(list.size(), resultList.size());
    }

    @org.junit.jupiter.api.Test
    void testFindTopAuthorByLendings() {
        // Arrange
        AuthorLendingView mockView = mock(AuthorLendingView.class);
        List<AuthorLendingView> list = List.of(mockView);

        Pageable pageable = PageRequest.of(0, 10);

        when(authoRepo.findTopAuthorByLendings(pageable)).thenReturn(list);

        // Act
        List<AuthorLendingView> result = authoRepo.findTopAuthorByLendings(pageable);

        // Assert
        assertEquals(list.size(), result.size());
    }

    @org.junit.jupiter.api.Test
    void testFindCoAuthorsByAuthorNumber()
    {
        // Arrange
        Author mockAuthorCache = mock(Author.class);
        List<Author> cached = new ArrayList<>();
        cached.add(mockAuthorCache);

        List<AuthorEntity> list = new ArrayList<>();
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        list.add(mockAuthorEntity);

        when(redisRepo.getAuthorListFromRedis(anyString())).thenReturn(cached);
        when(sqlRepo.findCoAuthorsByAuthorNumber(anyString())).thenReturn(list);

        Author mockAuthor = mock(Author.class);
        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);

        // Act
        List<Author> aut = authoRepo.findCoAuthorsByAuthorNumber(anyString());
        List<Author> autcache = redisRepo.getAuthorListFromRedis(anyString());
        doNothing().when(redisRepo).cacheAuthorListToRedis(anyString(), eq(autcache));

        // Assert
        assertEquals(list.size(), aut.size());
        assertEquals(cached.size(), autcache.size());
        assertTrue(autcache.contains(mockAuthorCache));
    }

    @org.junit.jupiter.api.Test
    void testFindCoAuthorsByAuthorNumberEmpty()
    {
        // Arrange
        List<Author> cached = new ArrayList<>();
        List<AuthorEntity> list = new ArrayList<>();

        when(redisRepo.getAuthorListFromRedis(anyString())).thenReturn(cached);
        when(sqlRepo.findCoAuthorsByAuthorNumber(anyString())).thenReturn(list);

        // Act
        List<Author> aut = authoRepo.findCoAuthorsByAuthorNumber(anyString());
        List<Author> autcache = redisRepo.getAuthorListFromRedis(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
        assertEquals(cached.size(), autcache.size());
        assertTrue(aut.isEmpty());
    }

    // ------------------- SAVES -------------------
    @org.junit.jupiter.api.Test
    void testSaveAuthor()
    {
        // Arrange
        Author mockAuthor = mock(Author.class);
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        when(authorEntityMapper.toEntity(mockAuthor)).thenReturn(mockAuthorEntity);

        when(sqlRepo.save(mockAuthorEntity)).thenReturn(mockAuthorEntity);

        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);
        doNothing().when(redisRepo).save(mockAuthor);
        // Act
        Author auto = authoRepo.save(mockAuthor);

        // Assert
        assertEquals(mockAuthor, auto);
    }

    // ------------------- DELETE -------------------
    //@org.junit.jupiter.api.Test
    //void testDelete()
    //{
    //    // Arrange
    //    Author mockAuthor = mock(Author.class);
    //    AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
    //    when(authorEntityMapper.toEntity(mockAuthor)).thenReturn(mockAuthorEntity);
//
    //    // Act
    //    authoRepo.delete(mockAuthor);
//
    //    // Assert
    //    verify(sqlRepo).delete(mockAuthorEntity);
    //}
}
