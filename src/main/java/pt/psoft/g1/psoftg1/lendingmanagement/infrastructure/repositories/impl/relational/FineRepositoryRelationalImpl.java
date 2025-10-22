package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.relational;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mappers.FineEntityMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.FineEntity;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class FineRepositoryRelationalImpl implements FineRepository
{
    private final SpringDataFineRepository fineRepo;
    private final FineEntityMapper fineEntityMapper;

    @Override
    public Optional<Fine> findByLendingNumber(String lendingNumber)
    {
        Optional<FineEntity> entityOpt = fineRepo.findByLendingNumber(lendingNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(fineEntityMapper.toModel(entityOpt.get()));
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
        for (FineEntity f: fineRepo.findAll())
        {
            fines.add(fineEntityMapper.toModel(f));
        }

        return fines;
    }

    @Override
    public Fine save(Fine fine)
    {
        return fineEntityMapper.toModel(fineRepo.save(fineEntityMapper.toEntity(fine)));
    }

}
