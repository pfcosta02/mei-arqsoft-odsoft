package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoMapperMongoDB;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@Primary
@RequiredArgsConstructor
public class PhotoRepositoryMongoDBImpl implements PhotoRepository {

    private final SpringDataPhotoRepositoryMongoDB photoRepositoryMongoDB;
    private final PhotoMapperMongoDB photoMapperMongoDB;

    @Override
    public void deleteByPhotoFile(String photoFile) {
        photoRepositoryMongoDB.deleteByPhotoFile(photoFile);
    }
}
