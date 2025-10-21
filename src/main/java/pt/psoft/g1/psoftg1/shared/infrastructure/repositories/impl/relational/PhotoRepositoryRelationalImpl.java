package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.PhotoEntityMapper;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

@Profile("jpa")
@Primary
@RequiredArgsConstructor
@Repository
public class PhotoRepositoryRelationalImpl implements PhotoRepository
{
    private final SpringDataPhotoRepository photoRepository;
    private final PhotoEntityMapper photoEntityMapper;

    @Override
    public void deleteByPhotoFile(String photoFile)
    {
        photoRepository.deleteByPhotoFile(photoFile);
    }
}
