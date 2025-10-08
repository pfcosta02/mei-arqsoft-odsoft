package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.FineEntity;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;

@Profile("jpa")
@Primary
@RequiredArgsConstructor
public class ReaderDetailsRepositoryRelationalImpl implements ReaderRepository
{
    private final SpringDataReaderRepositoryImpl readerRepo;
    private final ReaderDetailsEntityMapper readerEntityMapper;
    private final EntityManager entityManager;

    @Override
    public Optional<ReaderDetails> findByReaderNumber(@Param("readerNumber") @NotNull String readerNumber)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerRepo.findByReaderNumber(readerNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<ReaderDetails> findByPhoneNumber(@Param("phoneNumber") @NotNull String phoneNumber)
    {
        List<ReaderDetails> readers = new ArrayList<>();
        for (ReaderDetailsEntity r: readerRepo.findByPhoneNumber(phoneNumber))
        {
            readers.add(readerEntityMapper.toModel(r));
        }

        return readers;
    }

    @Override
    public Optional<ReaderDetails> findByUsername(@Param("username") @NotNull String username)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerRepo.findByUsername(username);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReaderDetails> findByUserId(@Param("userId") @NotNull Long userId)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerRepo.findByUserId(userId);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public int getCountFromCurrentYear()
    {
        return readerRepo.getCountFromCurrentYear();
    }

    @Override
    public ReaderDetails save(ReaderDetails readerDetails)
    {
        // TODO
        return readerDetails;
    }

    @Override
    public Iterable<ReaderDetails> findAll()
    {
        List<ReaderDetails> readerDetails = new ArrayList<>();
        for (ReaderDetailsEntity r: readerRepo.findAll())
        {
            readerDetails.add(readerEntityMapper.toModel(r));
        }

        return readerDetails;
    }

    @Override
    public Page<ReaderDetails> findTopReaders(Pageable pageable)
    {
        return readerRepo.findTopReaders(pageable).map(readerEntityMapper::toModel);
    }

    @Override
    public Page<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate)
    {
        return readerRepo.findTopByGenre(pageable, genre, startDate, endDate);
    }

    @Override
    public void delete(ReaderDetails readerDetails)
    {
        // TODO
    }

    @Override
    public List<ReaderDetails> searchReaderDetails(pt.psoft.g1.psoftg1.shared.services.Page page, SearchReadersQuery query)
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ReaderDetailsEntity> cq = cb.createQuery(ReaderDetailsEntity.class);
        final Root<ReaderDetailsEntity> readerDetailsRoot = cq.from(ReaderDetailsEntity.class);
        Join<ReaderDetailsEntity, UserEntity> userJoin = readerDetailsRoot.join("reader");

        cq.select(readerDetailsRoot);

        final List<Predicate> where = new ArrayList<>();
        if (StringUtils.hasText(query.getName()))
        {
            //'contains' type search
            where.add(cb.like(userJoin.get("name").get("name"), "%" + query.getName() + "%"));
            cq.orderBy(cb.asc(userJoin.get("name")));
        }
        if (StringUtils.hasText(query.getEmail()))
        {
            //'exatct' type search
            where.add(cb.equal(userJoin.get("username"), query.getEmail()));
            cq.orderBy(cb.asc(userJoin.get("username")));
        }
        if (StringUtils.hasText(query.getPhoneNumber()))
        {
            //'exatct' type search
            where.add(cb.equal(readerDetailsRoot.get("phoneNumber").get("phoneNumber"), query.getPhoneNumber()));
            cq.orderBy(cb.asc(readerDetailsRoot.get("phoneNumber").get("phoneNumber")));
        }

        // search using OR
        if (!where.isEmpty())
        {
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }


        final TypedQuery<ReaderDetailsEntity> q = entityManager.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List<ReaderDetails> readerDetails = new ArrayList<>();

        for (ReaderDetailsEntity readerDetail : q.getResultList())
        {
            readerDetails.add(readerEntityMapper.toModel(readerDetail));
        }

        return readerDetails;
    }
}

