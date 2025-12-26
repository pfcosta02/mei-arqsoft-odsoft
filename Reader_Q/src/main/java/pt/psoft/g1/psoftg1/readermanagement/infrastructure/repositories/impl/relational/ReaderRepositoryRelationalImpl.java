package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.Reader;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("sql")
@Primary
@Repository
@RequiredArgsConstructor
public class ReaderRepositoryRelationalImpl implements ReaderRepository {
    private final SpringDataReaderRepositoryImpl readerRepo;
    private final ReaderEntityMapper readerEntityMapper;

    @Override
    public Reader save(Reader reader)
    {
        ReaderEntity entity = readerEntityMapper.toEntity(reader);
        ReaderEntity savedEntity = readerRepo.save(entity);

        return readerEntityMapper.toModel(savedEntity);
    }

    @Override
    public Optional<Reader> findByEmail(String email)
    {
        Optional<ReaderEntity> entity = readerRepo.findByEmail(email);
        if (entity.isPresent())
        {
            return Optional.of(readerEntityMapper.toModel(entity.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<Reader> searchByName(String namePart)
    {
        List<Reader> readers = new ArrayList();

        for (ReaderEntity r: readerRepo.searchByName(namePart))
        {
            readers.add(readerEntityMapper.toModel(r));
        }

        return readers;
    }

    @Override
    public void delete(String readerId)
    {
        readerRepo.deleteById(readerId);
    }
}
