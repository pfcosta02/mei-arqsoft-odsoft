package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.LendingMapperMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.shared.services.Page;

@Profile("mongodb")
@Primary
@RequiredArgsConstructor
@Repository
public class LendingRepositoryMongoDBImpl implements LendingRepository
{
    private final SpringDataLendingRepositoryMongoDB lendingRepo;
    private final LendingMapperMongoDB lendingMapperMongoDB;
    private final MongoTemplate mongoTemplate;

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
    public List<Lending> getOverdue(Page page) {
        Query query = new Query();

        // returnedDate == null
        query.addCriteria(Criteria.where("returnedDate").is(null));
        // limitDate < today
        query.addCriteria(Criteria.where("limitDate").lt(LocalDate.now()));

        // order by limitDate ascending
        query.with(Sort.by(Sort.Direction.ASC, "limitDate"));

        // pagination
        query.skip((page.getNumber() - 1L) * page.getLimit());
        query.limit(page.getLimit());

        List<LendingMongoDB> results = mongoTemplate.find(query, LendingMongoDB.class);

        return results.stream()
                .map(lendingMapperMongoDB::toModel)
                .toList();
    }

    @Override
    public List<Lending> searchLendings(Page page, String readerNumber, String isbn,
                                        Boolean returned, LocalDate startDate, LocalDate endDate) {
        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (StringUtils.hasText(readerNumber)) {
            criteria.add(Criteria.where("readerDetails.readerNumber").is(readerNumber));
        }

        if (StringUtils.hasText(isbn)) {
            criteria.add(Criteria.where("book.isbn").is(isbn));
        }

        if (returned != null) {
            if (returned) {
                criteria.add(Criteria.where("returnedDate").ne(null));
            } else {
                criteria.add(Criteria.where("returnedDate").is(null));
            }
        }

        if (startDate != null) {
            criteria.add(Criteria.where("startDate").gte(startDate));
        }

        if (endDate != null) {
            criteria.add(Criteria.where("startDate").lte(endDate));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        query.with(Sort.by(Sort.Direction.ASC, "lendingNumber"));
        query.skip((page.getNumber() - 1L) * page.getLimit());
        query.limit(page.getLimit());

        List<LendingMongoDB> results = mongoTemplate.find(query, LendingMongoDB.class);

        return results.stream()
                .map(lendingMapperMongoDB::toModel)
                .toList();
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