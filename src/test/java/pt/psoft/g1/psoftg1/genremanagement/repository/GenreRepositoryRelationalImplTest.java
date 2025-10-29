package pt.psoft.g1.psoftg1.genremanagement.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.Tuple;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreEntityMapper;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational.GenreRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational.SpringDataGenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;

class GenreRepositoryRelationalImplTest {

    @InjectMocks
    private GenreRepositoryRelationalImpl genreRepo;

    @Mock
    private SpringDataGenreRepository springDataGenreRepo;

    @Mock
    private GenreEntityMapper genreEntityMapper;

    @Mock
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllGenres() {
        GenreEntity genreEntity = mock(GenreEntity.class);
        Genre genre = mock(Genre.class);

        List<GenreEntity> entities = List.of(genreEntity);
        when(springDataGenreRepo.findAll()).thenReturn(entities);
        when(genreEntityMapper.toModel(genreEntity)).thenReturn(genre);

        Iterable<Genre> result = genreRepo.findAll();

        assertTrue(result.iterator().hasNext());
        assertEquals(genre, result.iterator().next());
    }

    @Test
    void testFindAllGenresEmpty() {
        when(springDataGenreRepo.findAll()).thenReturn(Collections.emptyList());

        Iterable<Genre> result = genreRepo.findAll();

        assertTrue(!result.iterator().hasNext());
    }

    @Test
    void testFindByStringPresent() {
        GenreEntity genreEntity = mock(GenreEntity.class);
        Genre genre = mock(Genre.class);

        when(springDataGenreRepo.findByString("Fiction")).thenReturn(Optional.of(genreEntity));
        when(genreEntityMapper.toModel(genreEntity)).thenReturn(genre);

        Optional<Genre> result = genreRepo.findByString("Fiction");

        assertTrue(result.isPresent());
        assertEquals(genre, result.get());
    }

    @Test
    void testFindByStringEmpty() {
        when(springDataGenreRepo.findByString("Unknown")).thenReturn(Optional.empty());

        Optional<Genre> result = genreRepo.findByString("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void testSaveGenre() {
        Genre genre = mock(Genre.class);
        GenreEntity genreEntity = mock(GenreEntity.class);

        when(genreEntityMapper.toEntity(genre)).thenReturn(genreEntity);
        when(springDataGenreRepo.save(genreEntity)).thenReturn(genreEntity);
        when(genreEntityMapper.toModel(genreEntity)).thenReturn(genre);

        Genre savedGenre = genreRepo.save(genre);

        assertEquals(genre, savedGenre);
    }

    @Test
    void testFindTop5GenreByBookCount() {
        GenreBookCountDTO dto = mock(GenreBookCountDTO.class);
        Pageable pageable = PageRequest.of(0, 5);
        List<GenreBookCountDTO> list = List.of(dto);

        when(springDataGenreRepo.findTop5GenreByBookCount(pageable)).thenReturn(list);

        List<GenreBookCountDTO> result = genreRepo.findTop5GenreByBookCount(pageable);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    // @Test
    // void testDeleteGenre() {
    //     Genre genre = mock(Genre.class);
    //     GenreEntity genreEntity = mock(GenreEntity.class);

    //     when(genreEntityMapper.toEntity(genre)).thenReturn(genreEntity);

    //     genreRepo.delete(genre);
    // }

    @Test
    void testGetLendingsAverageDurationPerMonth() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<Tuple> cq = mock(CriteriaQuery.class);

        @SuppressWarnings("unchecked")
        Root<LendingEntity> lendingRoot = mock(Root.class);

        @SuppressWarnings("unchecked")
        Join<Object, Object> bookJoin = mock(Join.class);

        @SuppressWarnings("unchecked")
        Join<Object, Object> genreJoin = mock(Join.class);

        @SuppressWarnings("unchecked")
        TypedQuery<Tuple> typedQuery = mock(TypedQuery.class);
        Tuple tuple = mock(Tuple.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createTupleQuery()).thenReturn(cq);
        when(cq.from(LendingEntity.class)).thenReturn(lendingRoot);
        when(lendingRoot.join("book")).thenReturn(bookJoin);
        when(bookJoin.join("genre")).thenReturn(genreJoin);

        when(entityManager.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(tuple));

        when(tuple.get(0, String.class)).thenReturn("Fiction");
        when(tuple.get(1, Integer.class)).thenReturn(2024);
        when(tuple.get(2, Integer.class)).thenReturn(5);
        when(tuple.get(3, Double.class)).thenReturn(4.2);

        // Act
        List<GenreLendingsPerMonthDTO> result = genreRepo.getLendingsAverageDurationPerMonth(startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        GenreLendingsPerMonthDTO dto = result.get(0);
        assertEquals(2024, dto.getYear());
        assertEquals(5, dto.getMonth());
        assertEquals("Fiction", dto.getValues().get(0).getGenre());
        assertEquals(4.2, dto.getValues().get(0).getValue());
    }

    
    @Test
    void testGetLendingsPerMonthLastYearByGenre() {
        // Arrange
        Tuple mockTuple = mock(Tuple.class);
        when(mockTuple.get(0, String.class)).thenReturn("Fantasy");
        when(mockTuple.get(1, Integer.class)).thenReturn(2025);
        when(mockTuple.get(2, Integer.class)).thenReturn(10);
        when(mockTuple.get(3, Long.class)).thenReturn(5L);

        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        @SuppressWarnings("unchecked")
        CriteriaQuery<Tuple> criteriaQuery = mock(CriteriaQuery.class);
        @SuppressWarnings("unchecked")
        Root<LendingEntity> lendingRoot = mock(Root.class);
        @SuppressWarnings("unchecked")
        Join<Object, Object> bookJoin = mock(Join.class);
        @SuppressWarnings("unchecked")
        Join<Object, Object> genreJoin = mock(Join.class);

        @SuppressWarnings("unchecked")
        Expression<Integer> yearExpr = mock(Expression.class);
        @SuppressWarnings("unchecked")
        Expression<Integer> monthExpr = mock(Expression.class);
        @SuppressWarnings("unchecked")
        Expression<Long> countExpr = mock(Expression.class);
        Predicate datePredicate = mock(Predicate.class);
        @SuppressWarnings("unchecked")
        Path<Date> startDatePath = mock(Path.class);
        @SuppressWarnings("unchecked")
        TypedQuery<Tuple> typedQuery = mock(TypedQuery.class);
        @SuppressWarnings("unchecked")
        Expression<Date> startDateExpr = mock(Expression.class);
       
        Order orderYear = mock(Order.class);
        Order orderMonth = mock(Order.class);
        Order orderGenre = mock(Order.class);

        when(entityManager.getCriteriaBuilder()).thenReturn(criteriaBuilder);
        when(criteriaBuilder.createTupleQuery()).thenReturn(criteriaQuery);
        when(criteriaQuery.from(LendingEntity.class)).thenReturn(lendingRoot);
        when(lendingRoot.join("book")).thenReturn(bookJoin);
        when(bookJoin.join("genre")).thenReturn(genreJoin);

        when(criteriaBuilder.function("YEAR", Integer.class, lendingRoot.get("startDate"))).thenReturn(yearExpr);
        when(criteriaBuilder.function("MONTH", Integer.class, lendingRoot.get("startDate"))).thenReturn(monthExpr);
        when(criteriaBuilder.count(lendingRoot)).thenReturn(countExpr);

        when(lendingRoot.get("startDate")).thenReturn((Path)startDatePath);

        LocalDate now = LocalDate.now();
        LocalDate twelveMonthsAgo = now.minusMonths(12);
        Date nowDate = java.sql.Date.valueOf(now);
        Date twelveMonthsAgoDate = java.sql.Date.valueOf(twelveMonthsAgo);

        when(criteriaBuilder.between(startDateExpr, twelveMonthsAgoDate, nowDate)).thenReturn(datePredicate);

        when(criteriaQuery.multiselect(genreJoin.get("genre"), yearExpr, monthExpr, countExpr)).thenReturn(criteriaQuery);
        when(criteriaQuery.groupBy(genreJoin.get("genre"), yearExpr, monthExpr)).thenReturn(criteriaQuery);
        when(criteriaQuery.where(datePredicate)).thenReturn(criteriaQuery);

        when(criteriaBuilder.asc(yearExpr)).thenReturn(orderYear);
        when(criteriaBuilder.asc(monthExpr)).thenReturn(orderMonth);
        when(criteriaBuilder.asc(genreJoin.get("genre"))).thenReturn(orderGenre);
        when(criteriaQuery.orderBy(orderYear, orderMonth, orderGenre)).thenReturn(criteriaQuery);

        when(entityManager.createQuery(criteriaQuery)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(List.of(mockTuple));


        // Act
        List<GenreLendingsPerMonthDTO> result = genreRepo.getLendingsPerMonthLastYearByGenre();

        // Assert
        assertEquals(1, result.size());
        GenreLendingsPerMonthDTO dto = result.get(0);
        assertEquals(2025, dto.getYear());
        assertEquals(10, dto.getMonth());
        assertEquals(1, dto.getValues().size());
        assertEquals("Fantasy", dto.getValues().get(0).getGenre());
    }


}
