package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingMapperMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.shared.services.Page;

@Profile("mongodb")
@Primary
@RequiredArgsConstructor
public class LendingRepositoryMongoDBImpl implements LendingRepository
{
    private final SpringDataLendingRepositoryMongoDB lendingRepo;
    private final LendingMapperMongoDB lendingMapperMongoDB;
    private final EntityManager em;

    @Override
    public Optional<Lending> findByLendingNumber(String lendingNumber)
    {
        Optional<LendingMongoDB> entityOpt = lendingRepo.findByLendingNumber(lendingNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(lendingMapperMongoDB.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<Lending> listByReaderNumberAndIsbn(String readerNumber, String isbn)
    {
        List<Lending> lendings = new ArrayList<>();
        for (LendingMongoDB l: lendingRepo.listByReaderNumberAndIsbn(readerNumber, isbn))
        {
            lendings.add(lendingMapperMongoDB.toModel(l));
        }

        return lendings;
    }

    @Override
    public int getCountFromCurrentYear()
    {
        return lendingRepo.getCountFromCurrentYear();
    }

    @Override
    public List<Lending> listOutstandingByReaderNumber(String readerNumber)
    {
        List<Lending> lendings = new ArrayList<>();
        for (LendingMongoDB l: lendingRepo.listOutstandingByReaderNumber(readerNumber))
        {
            lendings.add(lendingMapperMongoDB.toModel(l));
        }

        return lendings;
    }

    @Override
    public Double getAverageDuration()
    {
        return lendingRepo.getAverageDuration();
    }

    @Override
    public Double getAvgLendingDurationByIsbn(String isbn)
    {
        return lendingRepo.getAvgLendingDurationByIsbn(isbn);
    }

    @Override
    public List<Lending> getOverdue(Page page)
    {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<LendingMongoDB> cq = cb.createQuery(LendingMongoDB.class);
        final Root<LendingMongoDB> root = cq.from(LendingMongoDB.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();

        // Select overdue lendings where returnedDate is null and limitDate is before the current date
        where.add(cb.isNull(root.get("returnedDate")));
        where.add(cb.lessThan(root.get("limitDate"), LocalDate.now()));

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("limitDate"))); // Order by limitDate, oldest first

        final TypedQuery<LendingMongoDB> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List<Lending> lendings = new ArrayList<>();

        for (LendingMongoDB lendingMongoDB : q.getResultList()) {
            lendings.add(lendingMapperMongoDB.toModel(lendingMongoDB));
        }

        return lendings;
    }

    @Override
    public List<Lending> searchLendings(Page page, String readerNumber, String isbn, Boolean returned, LocalDate startDate, LocalDate endDate)
    {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<LendingMongoDB> cq = cb.createQuery(LendingMongoDB.class);
        final Root<LendingMongoDB> lendingRoot = cq.from(LendingMongoDB.class);
        final Join<LendingMongoDB, Book> bookJoin = lendingRoot.join("book");
        final Join<LendingMongoDB, ReaderDetails> readerDetailsJoin = lendingRoot.join("readerDetails");
        cq.select(lendingRoot);

        final List<Predicate> where = new ArrayList<>();

        if (StringUtils.hasText(readerNumber))
        {
            where.add(cb.like(readerDetailsJoin.get("readerNumber").get("readerNumber"), readerNumber));
        }
        if (StringUtils.hasText(isbn))
        {
            where.add(cb.like(bookJoin.get("isbn").get("isbn"), isbn));
        }
        if (returned != null)
        {
            if(returned)
            {
                where.add(cb.isNotNull(lendingRoot.get("returnedDate")));
            }
            else
            {
                where.add(cb.isNull(lendingRoot.get("returnedDate")));
            }
        }
        if(startDate!=null)
        {
            where.add(cb.greaterThanOrEqualTo(lendingRoot.get("startDate"), startDate));
        }
        if(endDate!=null)
        {
            where.add(cb.lessThanOrEqualTo(lendingRoot.get("startDate"), endDate));
        }

        cq.where(where.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(lendingRoot.get("lendingNumber")));

        final TypedQuery<LendingMongoDB> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List<Lending> lendings = new ArrayList<>();

        for (LendingMongoDB lendingMongoDB : q.getResultList())
        {
            lendings.add(lendingMapperMongoDB.toModel(lendingMongoDB));
        }

        return lendings;
    }

    @Override
    public Lending save(Lending lending)
    {
        return lendingMapperMongoDB.toModel(lendingRepo.save(lendingMapperMongoDB.toEntity(lending)));
    }

    @Override
    public void delete(Lending lending)
    {
        lendingRepo.delete(lendingMapperMongoDB.toEntity(lending));
    }
}