package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderDetailsRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderEntity;


@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class ReaderDetailsRepositoryRelationalImpl implements ReaderDetailsRepository
{
    private final SpringDataReaderDetailsRepositoryImpl readerDetailsRepo;
    private final ReaderDetailsEntityMapper readerDetailsEntityMapper;
    private final ReaderRepositoryRelationalImpl readerRepo;
    private final EntityManager entityManager;

    @Override
    public Optional<ReaderDetails> findByReaderNumber(String readerNumber)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerDetailsRepo.findByReaderNumber(readerNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerDetailsEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }


    @Override
    public Optional<ReaderDetails> findByEmail(String email)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerDetailsRepo.findByEmail(email);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerDetailsEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReaderDetails> findByUserId(String userId)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerDetailsRepo.findByUserId(userId);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerDetailsEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public int getCountFromCurrentYear()
    {
        return readerDetailsRepo.getCountFromCurrentYear();
    }

    @Override
    public ReaderDetails save(ReaderDetails readerDetails)
    {
        // Convert the domain model (readerDetails) to a JPA entity (ReaderDetailsEntity)
        ReaderDetailsEntity readerDetailsEntity = readerDetailsEntityMapper.toEntity(readerDetails);

        // Retrieve the existing Reader model from the repository
        // Throws an exception if the reader is not found
        Reader reader = readerRepo.findByEmail(readerDetails.getReader().getEmail())
                .orElseThrow(() -> new RuntimeException("Reader not found"));

        // Get the managed JPA reference for the UserEntity using its database ID
        // This ensures we use the existing UserEntity instead of creating a new one
        ReaderEntity readerEntity = entityManager.getReference(ReaderEntity.class, reader.getReaderId());

        readerDetailsEntity.setReader(readerEntity);
        return readerDetailsEntityMapper.toModel(readerDetailsRepo.save(readerDetailsEntity));
    }


    @Override
    public void delete(ReaderDetails readerDetails)
    {
        readerDetailsRepo.delete(readerDetailsEntityMapper.toEntity(readerDetails));
    }

    @Override
    public List<ReaderDetails> searchReaderDetails(pt.psoft.g1.psoftg1.shared.services.Page page, SearchReadersQuery query)
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<ReaderDetailsEntity> cq = cb.createQuery(ReaderDetailsEntity.class);
        final Root<ReaderDetailsEntity> readerDetailsRoot = cq.from(ReaderDetailsEntity.class);
        Join<ReaderDetailsEntity, String> userJoin = readerDetailsRoot.join("reader");

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
            readerDetails.add(readerDetailsEntityMapper.toModel(readerDetail));
        }

        return readerDetails;
    }

    @Override
    public List<ReaderDetails> findAll()
    {
        List<ReaderDetails> rd = new ArrayList<>();
        for (ReaderDetailsEntity rd_entity: readerDetailsRepo.findAll())
        {
            rd.add(readerDetailsEntityMapper.toModel(rd_entity));
        }

        return rd;
    }
}