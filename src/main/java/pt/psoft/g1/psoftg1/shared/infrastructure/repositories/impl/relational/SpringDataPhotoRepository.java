package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pt.psoft.g1.psoftg1.shared.model.relational.PhotoEntity;

public interface SpringDataPhotoRepository extends CrudRepository<PhotoEntity, Long> {

    @Modifying
    @Transactional
    @Query("DELETE " +
            "FROM PhotoEntity p " +
            "WHERE p.photoFile = :photoFile")
    void deleteByPhotoFile(String photoFile);
}
