package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderTempEntity;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderTempRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;

import java.util.Optional;

@Profile("sql")
@Primary
@Repository
@RequiredArgsConstructor
public class ReaderTempRepositoryRelationalImpl implements ReaderTempRepository {
    private final SpringDataReaderTempRepositoryImpl readerTempRepo;
    private final ReaderEntityMapper readerEntityMapper;


    @Override
    public Reader save(Reader reader)
    {
        ReaderTempEntity entity = readerEntityMapper.toTempEntity(reader);
        ReaderTempEntity savedEntity = readerTempRepo.save(entity);

        return readerEntityMapper.toModelFromTemp(savedEntity);
    }

    @Override
    public void delete(String readerId)
    {
        readerTempRepo.deleteById(readerId);
    }

    @Override
    public Optional<Reader> findByReaderId(String readerId)
    {
        Optional<ReaderTempEntity> entity = readerTempRepo.findByReaderId(readerId);
        if (entity.isPresent())
        {
            return Optional.of(readerEntityMapper.toModelFromTemp(entity.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Reader> findByEmail(String email)
    {
        Optional<ReaderTempEntity> entity = readerTempRepo.findByEmail(email);
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
