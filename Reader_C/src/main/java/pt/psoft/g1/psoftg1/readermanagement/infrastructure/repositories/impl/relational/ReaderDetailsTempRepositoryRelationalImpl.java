package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsTempEntity;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderDetailsTempRepository;

import java.util.Optional;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class ReaderDetailsTempRepositoryRelationalImpl implements ReaderDetailsTempRepository {
    private final ReaderDetailsEntityMapper readerEntityMapper;

    private final SpringDataReaderDetailsTempRepositoryImpl readerDetailsTempoRepo;

    @Override
    public ReaderDetails save(ReaderDetails readerDetails)
    {
        ReaderDetailsTempEntity tempReader = readerEntityMapper.toTempEntity(readerDetails);
        ReaderDetailsTempEntity savedEntity = readerDetailsTempoRepo.save(tempReader);

        return readerEntityMapper.toModelFromTemp(savedEntity);
    }

    @Override
    public void delete(String readerId)
    {
        readerDetailsTempoRepo.deleteById(readerId);
    }

    @Override
    public Optional<ReaderDetails> findByReaderId(String readerId)
    {
        Optional<ReaderDetailsTempEntity> entity = readerDetailsTempoRepo.findByReaderId(readerId);
        if (entity.isPresent())
        {
            return Optional.of(readerEntityMapper.toModelFromTemp(entity.get()));
        }
        else
        {
            return Optional.empty();
        }
    }
}