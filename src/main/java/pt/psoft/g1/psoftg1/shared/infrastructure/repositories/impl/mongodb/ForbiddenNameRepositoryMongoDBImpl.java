package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.mongodb.ForbiddenNameMongoDB;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.ForbiddenNameMapperMongoDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@RequiredArgsConstructor
@Repository
public class ForbiddenNameRepositoryMongoDBImpl implements ForbiddenNameRepository {

    private final SpringDataForbiddenNameRepositoryMongoDB forbiddenNameRepositoryMongoDB;
    private final ForbiddenNameMapperMongoDB forbiddenNameMapperMongoDB;

    @Override
    public Iterable<ForbiddenName> findAll()
    {
        List<ForbiddenName> fn = new ArrayList<>();
        for (ForbiddenNameMongoDB f: forbiddenNameRepositoryMongoDB.findAll())
        {
            fn.add(forbiddenNameMapperMongoDB.toModel(f));
        }

        return fn;
    }

    @Override
    public List<ForbiddenName> findByForbiddenNameIsContained(String pat)
    {
        List<ForbiddenName> fn = new ArrayList<>();
        for (ForbiddenNameMongoDB f: forbiddenNameRepositoryMongoDB.findByForbiddenNameIsContained(pat))
        {
            fn.add(forbiddenNameMapperMongoDB.toModel(f));
        }

        return fn;
    }

    @Override
    public ForbiddenName save(ForbiddenName forbiddenName)
    {
        return forbiddenNameMapperMongoDB.toModel(forbiddenNameRepositoryMongoDB.save(forbiddenNameMapperMongoDB.toMongoDB(forbiddenName)));
    }

    @Override
    public Optional<ForbiddenName> findByForbiddenName(String forbiddenName)
    {
        Optional<ForbiddenNameMongoDB> entityOpt = forbiddenNameRepositoryMongoDB.findByForbiddenName(forbiddenName);
        if (entityOpt.isPresent())
        {
            return Optional.of(forbiddenNameMapperMongoDB.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public int deleteForbiddenName(String forbiddenName)
    {
        return forbiddenNameRepositoryMongoDB.deleteForbiddenName(forbiddenName);
    }
}