package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.FineMapperMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoDB;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;

@Profile("mongodb")
@RequiredArgsConstructor
@Repository
public class FineRepositoryMongoDBImpl implements FineRepository
{
    private final SpringDataFineRepositoryMongoDB fineRepo;
    private final FineMapperMongoDB fineMapperMongoDB;

    @Override
    public Optional<Fine> findByLendingNumber(String lendingNumber)
    {
        Optional<FineMongoDB> entityOpt = fineRepo.findByLendingNumber(lendingNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(fineMapperMongoDB.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Fine> findAll()
    {
        List<Fine> fines = new ArrayList<>();
        for (FineMongoDB f: fineRepo.findAll())
        {
            fines.add(fineMapperMongoDB.toModel(f));
        }

        return fines;
    }

    @Override
    public Fine save(Fine fine)
    {
        return fineMapperMongoDB.toModel(fineRepo.save(fineMapperMongoDB.toEntity(fine)));
    }
}