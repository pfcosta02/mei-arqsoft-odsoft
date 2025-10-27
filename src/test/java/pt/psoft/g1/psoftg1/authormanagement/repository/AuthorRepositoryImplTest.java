package pt.psoft.g1.psoftg1.authormanagement.repository;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.AuthorRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.SpringDataAuthorRepository;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;

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
class AuthorRepositoryImplTest 
{
    @InjectMocks
    private AuthorRepositoryRelationalImpl authoRepo;

    @Mock
    private SpringDataAuthorRepository sqlRepo;

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

        AuthorEntity authorEntity = mock(AuthorEntity.class);
        when(sqlRepo.findByAuthorNumber(anyString())).thenReturn(Optional.of(authorEntity));
        when(authorEntityMapper.toModel(authorEntity)).thenReturn(mockAuthor);

        // Act
        Optional<Author> aut = authoRepo.findByAuthorNumber(anyString());

        // Assert
        assertEquals(mockAuthor, aut.get());
    }

    @org.junit.jupiter.api.Test
    void testFindByInvalidAuthorNumber()
    {
        // Arrange
        when(sqlRepo.findByAuthorNumber(anyString())).thenReturn( Optional.empty());

        // Act
        Optional<Author> aut = authoRepo.findByAuthorNumber(anyString());

        // Assert
        assertEquals(Optional.empty(), aut);
    }

    @org.junit.jupiter.api.Test
    void testSearchByNameNameStartsWith()
    {
        // Arrange
        List<AuthorEntity> list = new ArrayList<>();
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        list.add(mockAuthorEntity);

        when(sqlRepo.searchByNameNameStartsWith(anyString())).thenReturn(list);

        Author mockAuthor = mock(Author.class);
        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);

        // Act
        List<Author> aut = authoRepo.searchByNameNameStartsWith(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
    }

    @org.junit.jupiter.api.Test
    void testSearchByInvalidNameNameStartsWith()
    {
        // Arrange
        List<AuthorEntity> list = new ArrayList<>();

        when(sqlRepo.searchByNameNameStartsWith(anyString())).thenReturn(list);

        // Act
        List<Author> aut = authoRepo.searchByNameNameStartsWith(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
    }

    @org.junit.jupiter.api.Test
    void testSearchByName()
    {
        // Arrange
        List<AuthorEntity> list = new ArrayList<>();
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        list.add(mockAuthorEntity);

        when(sqlRepo.searchByNameName(anyString())).thenReturn(list);

        Author mockAuthor = mock(Author.class);
        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);

        // Act
        List<Author> aut = authoRepo.searchByNameName(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
    }

    @org.junit.jupiter.api.Test
    void testSearchByInvalidName()
    {
        // Arrange
        List<AuthorEntity> list = new ArrayList<>();

        when(sqlRepo.searchByNameName(anyString())).thenReturn(list);

        // Act
        List<Author> aut = authoRepo.searchByNameName(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
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
        List<AuthorEntity> list = new ArrayList<>();
        AuthorEntity mockAuthorEntity = mock(AuthorEntity.class);
        list.add(mockAuthorEntity);

        when(sqlRepo.findCoAuthorsByAuthorNumber(anyString())).thenReturn(list);

        Author mockAuthor = mock(Author.class);
        when(authorEntityMapper.toModel(mockAuthorEntity)).thenReturn(mockAuthor);

        // Act
        List<Author> aut = authoRepo.findCoAuthorsByAuthorNumber(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
        assertTrue(aut.contains(mockAuthor));
    }

    @org.junit.jupiter.api.Test
    void testFindCoAuthorsByAuthorNumberEmpty()
    {
        // Arrange
        List<AuthorEntity> list = new ArrayList<>();

        when(sqlRepo.findCoAuthorsByAuthorNumber(anyString())).thenReturn(list);

        // Act
        List<Author> aut = authoRepo.findCoAuthorsByAuthorNumber(anyString());

        // Assert
        assertEquals(list.size(), aut.size());
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
