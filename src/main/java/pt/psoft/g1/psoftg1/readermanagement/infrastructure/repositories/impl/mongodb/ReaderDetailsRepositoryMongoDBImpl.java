package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsMapperMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserMapperMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserReaderMapper;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb.UserRepositoryMongoDBImpl;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@Repository
@RequiredArgsConstructor
public class ReaderDetailsRepositoryMongoDBImpl implements ReaderRepository {

    private final SpringDataReaderRepositoryMongoDB readerRepo;
    private final ReaderDetailsMapperMongoDB readerMapperMongoDB;
    private final UserMapperMongoDB userMapperMongoDB;
    private final UserReaderMapper userReaderMapper;
    private final UserRepositoryMongoDBImpl userRepo;

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<ReaderDetails> findByReaderNumber(@Param("readerNumber") @NotNull String readerNumber)
    {
        Optional<ReaderDetailsMongoDB> entityOpt = readerRepo.findByReaderNumber(readerNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerMapperMongoDB.toModel(entityOpt.get()));
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
        for (ReaderDetailsMongoDB r: readerRepo.findByPhoneNumber(phoneNumber))
        {
            readers.add(readerMapperMongoDB.toModel(r));
        }

        return readers;
    }

    @Override
    public Optional<ReaderDetails> findByUsername(@Param("username") @NotNull String username)
    {
        Optional<ReaderDetailsMongoDB> entityOpt = readerRepo.findByUsername(username);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerMapperMongoDB.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReaderDetails> findByUserId(@Param("userId") @NotNull Long userId)
    {
        Optional<ReaderDetailsMongoDB> entityOpt = readerRepo.findByUserId(userId);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerMapperMongoDB.toModel(entityOpt.get()));
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
        ReaderDetailsMongoDB readerDetailsMongoDB = readerMapperMongoDB.toEntity(readerDetails);

        User userModel = userRepo.findByUsername(readerDetails.getReader().getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reader reader = UserReaderMapper.toReader(userModel);
        ReaderMongoDB readerMongoDB = userMapperMongoDB.toEntity(reader);
        readerDetailsMongoDB.setReader(readerMongoDB);
        return readerMapperMongoDB.toModel(readerRepo.save(readerDetailsMongoDB));
    }

    @Override
    public Iterable<ReaderDetails> findAll()
    {
        List<ReaderDetails> readerDetails = new ArrayList<>();
        for (ReaderDetailsMongoDB r: readerRepo.findAll())
        {
            readerDetails.add(readerMapperMongoDB.toModel(r));
        }

        return readerDetails;
    }

    @Override
    public List<ReaderDetails> findTopReaders(Pageable pageable) {
        return readerRepo.findTopReaders(pageable)
                .stream()
                .map(readerMapperMongoDB::toModel)
                .toList();
    }

    @Override
    public List<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate)
    {
        return readerRepo.findTopByGenre(pageable, genre, startDate, endDate);
    }

    @Override
    public void delete(ReaderDetails readerDetails)
    {
        // TODO
    }

    @Override
    public List<ReaderDetails> searchReaderDetails(pt.psoft.g1.psoftg1.shared.services.Page page, SearchReadersQuery query) {
        Query mongoQuery = new Query();
        List<Criteria> orCriteria = new ArrayList<>();

        // Buscar por nome (contains)
        if (StringUtils.hasText(query.getName())) {
            orCriteria.add(Criteria.where("reader.name").regex(query.getName(), "i")); // case-insensitive
            mongoQuery.with(Sort.by(Sort.Direction.ASC, "reader.name"));
        }

        // Buscar por email (exato)
        if (StringUtils.hasText(query.getEmail())) {
            orCriteria.add(Criteria.where("reader.username").is(query.getEmail()));
            mongoQuery.with(Sort.by(Sort.Direction.ASC, "reader.username"));
        }

        // Buscar por telefone (exato)
        if (StringUtils.hasText(query.getPhoneNumber())) {
            orCriteria.add(Criteria.where("phoneNumber").is(query.getPhoneNumber()));
            mongoQuery.with(Sort.by(Sort.Direction.ASC, "phoneNumber"));
        }

        // Combinar OR entre os filtros
        if (!orCriteria.isEmpty()) {
            mongoQuery.addCriteria(new Criteria().orOperator(orCriteria.toArray(new Criteria[0])));
        }

        // Paginação
        mongoQuery.skip((long) (page.getNumber() - 1) * page.getLimit());
        mongoQuery.limit(page.getLimit());

        // Executar query
        List<ReaderDetailsMongoDB> results = mongoTemplate.find(mongoQuery, ReaderDetailsMongoDB.class);

        // Mapear para modelo de domínio
        return results.stream()
                .map(readerMapperMongoDB::toModel)
                .toList();
    }
}