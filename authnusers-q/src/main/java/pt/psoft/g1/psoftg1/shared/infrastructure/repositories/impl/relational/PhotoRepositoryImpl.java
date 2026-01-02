package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.shared.repositories.PhotoRepository;

import org.springframework.stereotype.Repository;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class PhotoRepositoryImpl implements PhotoRepository
{
    private final SpringDataPhotoRepository photoRepository;

    @Override
    public void deleteByPhotoFile(String photoFile)
    {
        photoRepository.deleteByPhotoFile(photoFile);
    }
}
