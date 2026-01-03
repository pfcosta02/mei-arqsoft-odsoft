package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.relational;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational.BookRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingNumber;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational.ReaderDetailsRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.shared.services.Page;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
@Slf4j
public class LendingRepositoryRelationalImpl implements LendingRepository {
    private final SpringDataLendingRepository lendingRepo;
    private final LendingEntityMapper lendingEntityMapper;
    private final EntityManager em;
    private final BookRepositoryRelationalImpl bookRepo;
    private final ReaderDetailsRepositoryRelationalImpl readerDetailsRepo;

    private final BookEntityMapper bookEntityMapper;
    private final ReaderDetailsEntityMapper readerDetailsEntityMapper;

    @Override
    @Transactional
    public Lending returnLendingAndGetUpdated(
            String lendingNumber,
            LocalDate returnedDate,
            String commentary,
            Integer rating,
            long expectedVersion
    ) {
        int updated = lendingRepo.markReturned(
                lendingNumber,
                returnedDate,
                commentary,
                rating,
                expectedVersion
        );

        if (updated == 0) {
            throw new OptimisticLockException(
                    "Lending " + lendingNumber +
                            " was modified concurrently (version mismatch)"
            );
        }

        Optional<LendingEntity> entityOpt = lendingRepo.findByLendingNumber(lendingNumber);
        LendingEntity managedEntity = entityOpt.orElseThrow(
                () -> new RuntimeException("Lending not found: " + lendingNumber)
        );

        em.refresh(managedEntity);

        log.debug("After refresh - version: {}, commentary: {}, rating: {}",
                managedEntity.getVersion(),
                managedEntity.getCommentary(),
                managedEntity.getRating());

        return mapEntityToLending(managedEntity);
    }

    @Override
    public Optional<Lending> findByLendingNumber(String lendingNumber) {
        Optional<LendingEntity> entityOpt = lendingRepo.findByLendingNumber(lendingNumber);
        return entityOpt.map(this::mapEntityToLending);
    }

    // ⭐ Método auxiliar para mapear entidade para lending usando os mappers
    private Lending mapEntityToLending(LendingEntity entity) {
        // ⭐ Usa os mappers para converter Book e ReaderDetails
        Book bookModel = bookEntityMapper.toModel(entity.getBook());
        ReaderDetails readerDetailsModel = readerDetailsEntityMapper.toModel(entity.getReaderDetails());

        Lending lending = Lending.builder()
                .book(bookModel)
                .readerDetails(readerDetailsModel)
                .lendingNumber(new LendingNumber(entity.getLendingNumber().getLendingNumber()))
                .startDate(entity.getStartDate())
                .limitDate(entity.getLimitDate())
                .returnedDate(entity.getReturnedDate())
                .fineValuePerDayInCents(entity.getFineValuePerDayInCents())
                .commentary(entity.getCommentary())
                .rating(entity.getRating())
                .build();

        setFieldValue(lending, "pk", entity.getPk());
        setFieldValue(lending, "version", entity.getVersion());

        return lending;
    }

    // ⭐ Método auxiliar para reflexão
    private void setFieldValue(Lending lending, String fieldName, Object value) {
        try {
            var field = Lending.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(lending, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    @Override
    public List<Lending> listByReaderNumberAndIsbn(String readerNumber, String isbn) {
        List<Lending> lendings = new ArrayList<>();
        for (LendingEntity l: lendingRepo.listByReaderNumberAndIsbn(readerNumber, isbn)) {
            lendings.add(mapEntityToLending(l));
        }
        return lendings;
    }

    @Override
    public int getCountFromCurrentYear() {
        return lendingRepo.getCountFromCurrentYear();
    }

    @Override
    public List<Lending> listOutstandingByReaderNumber(String readerNumber) {
        List<Lending> lendings = new ArrayList<>();
        for (LendingEntity l: lendingRepo.listOutstandingByReaderNumber(readerNumber)) {
            lendings.add(mapEntityToLending(l));
        }
        return lendings;
    }

    @Override
    public Double getAverageDuration() {
        return lendingRepo.getAverageDuration();
    }

    @Override
    public Double getAvgLendingDurationByIsbn(String isbn) {
        return lendingRepo.getAvgLendingDurationByIsbn(isbn);
    }

    @Override
    public List<Lending> getOverdue(Page page) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<LendingEntity> cq = cb.createQuery(LendingEntity.class);
        final Root<LendingEntity> root = cq.from(LendingEntity.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();
        where.add(cb.isNull(root.get("returnedDate")));
        where.add(cb.lessThan(root.get("limitDate"), LocalDate.now()));

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("limitDate")));

        final TypedQuery<LendingEntity> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List<Lending> lendings = new ArrayList<>();
        for (LendingEntity lendingEntity : q.getResultList()) {
            lendings.add(mapEntityToLending(lendingEntity));
        }

        return lendings;
    }

    @Override
    public List<Lending> searchLendings(Page page, String readerNumber, String isbn, Boolean returned, LocalDate startDate, LocalDate endDate) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<LendingEntity> cq = cb.createQuery(LendingEntity.class);
        final Root<LendingEntity> lendingRoot = cq.from(LendingEntity.class);
        final Join<LendingEntity, Book> bookJoin = lendingRoot.join("book");
        final Join<LendingEntity, ReaderDetails> readerDetailsJoin = lendingRoot.join("readerDetails");
        cq.select(lendingRoot);

        final List<Predicate> where = new ArrayList<>();

        if (StringUtils.hasText(readerNumber)) {
            where.add(cb.like(readerDetailsJoin.get("readerNumber").get("readerNumber"), readerNumber));
        }
        if (StringUtils.hasText(isbn)) {
            where.add(cb.like(bookJoin.get("isbn").get("isbn"), isbn));
        }
        if (returned != null) {
            if(returned) {
                where.add(cb.isNotNull(lendingRoot.get("returnedDate")));
            } else {
                where.add(cb.isNull(lendingRoot.get("returnedDate")));
            }
        }
        if(startDate!=null) {
            where.add(cb.greaterThanOrEqualTo(lendingRoot.get("startDate"), startDate));
        }
        if(endDate!=null) {
            where.add(cb.lessThanOrEqualTo(lendingRoot.get("startDate"), endDate));
        }

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(lendingRoot.get("lendingNumber")));

        final TypedQuery<LendingEntity> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List<Lending> lendings = new ArrayList<>();
        for (LendingEntity lendingEntity : q.getResultList()) {
            lendings.add(mapEntityToLending(lendingEntity));
        }

        return lendings;
    }

    @Override
    public Lending save(Lending lending) {
        LendingEntity entity = lendingEntityMapper.toEntity(lending);

        Book bookModel = bookRepo.findByIsbn(lending.getBook().getIsbn().getIsbn())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        BookEntity bookEntity = em.getReference(BookEntity.class, bookModel.getPk());
        entity.setBook(bookEntity);

        ReaderDetails readerDetailsModel = readerDetailsRepo.findByReaderNumber(lending.getReaderDetails().getReaderNumber())
                .orElseThrow(() -> new RuntimeException("Reader not found"));
        ReaderDetailsEntity readerDetailsEntity = em.getReference(ReaderDetailsEntity.class, readerDetailsModel.getPk());
        entity.setReaderDetails(readerDetailsEntity);

        LendingEntity saved = lendingRepo.save(entity);

        setFieldValue(lending, "pk", saved.getPk());
        setFieldValue(lending, "version", saved.getVersion());

        return lending;
    }

    @Override
    public void delete(Lending lending) {
        lendingRepo.delete(lendingEntityMapper.toEntity(lending));
    }
}