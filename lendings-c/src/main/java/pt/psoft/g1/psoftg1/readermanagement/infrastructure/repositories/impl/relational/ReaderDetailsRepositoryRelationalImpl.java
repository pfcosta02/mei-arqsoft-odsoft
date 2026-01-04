package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;

import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.FineEntity;
import pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mappers.ReaderDetailsEntityMapper;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational.UserRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class ReaderDetailsRepositoryRelationalImpl implements ReaderRepository
{
    private final SpringDataReaderRepositoryImpl readerRepo;
    private final UserRepositoryRelationalImpl userRepo;
    private final ReaderDetailsEntityMapper readerEntityMapper;
    private final EntityManager entityManager;

    @Override
    public Optional<ReaderDetails> findByReaderNumber(String readerNumber)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerRepo.findByReaderNumber(readerNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }


    @Override
    public Optional<ReaderDetails> findByUsername(String username)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerRepo.findByUsername(username);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ReaderDetails> findByUserId(Long userId)
    {
        Optional<ReaderDetailsEntity> entityOpt = readerRepo.findByUserId(userId);
        if (entityOpt.isPresent())
        {
            return Optional.of(readerEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public int getCountFromCurrentYear()
    {
        return readerRepo.getCountFromCurrentYear();
    }

    @Override
    public ReaderDetails save(ReaderDetails readerDetails)
    {
        // Convert the domain model (readerDetails) to a JPA entity (ReaderDetailsEntity)
        ReaderDetailsEntity readerDetailsEntity = readerEntityMapper.toEntity(readerDetails);

        // Retrieve the existing User model from the repository
        // Throws an exception if the user is not found
        User userModel = userRepo.findByUsername(readerDetails.getReader().getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //TODO: No futuro aqui vai ter de deixar de ser ID
        // Get the managed JPA reference for the UserEntity using its database ID
        // This ensures we use the existing UserEntity instead of creating a new one
        ReaderEntity userEntity = entityManager.getReference(ReaderEntity.class, userModel.getId());

        readerDetailsEntity.setReader(userEntity);
        return readerEntityMapper.toModel(readerRepo.save(readerDetailsEntity));
    }

    @Override
    public Iterable<ReaderDetails> findAll()
    {
        List<ReaderDetails> readerDetails = new ArrayList<>();
        for (ReaderDetailsEntity r: readerRepo.findAll())
        {
            readerDetails.add(readerEntityMapper.toModel(r));
        }

        return readerDetails;
    }

    @Override
    public Page<ReaderDetails> findTopReaders(Pageable pageable)
    {
        return readerRepo.findTopReaders(pageable).map(readerEntityMapper::toModel);
    }


    @Override
    public void delete(ReaderDetails readerDetails)
    {
        readerRepo.delete(readerEntityMapper.toEntity(readerDetails));
    }

}

