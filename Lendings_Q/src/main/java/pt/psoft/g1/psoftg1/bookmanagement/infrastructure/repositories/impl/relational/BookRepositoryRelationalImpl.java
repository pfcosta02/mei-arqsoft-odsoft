package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;


import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;

import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class BookRepositoryRelationalImpl implements BookRepository
{
    private final SpringDataBookRepository bookRepo;
    private final BookEntityMapper bookEntityMapper;
    private final EntityManager em;


    @Override
    public Optional<Book> findByIsbn(@Param("isbn") String isbn)
    {
        Optional<BookEntity> entityOpt = bookRepo.findByIsbn(isbn);
        if(entityOpt.isPresent())
        {
            return Optional.of(bookEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Book save(Book book)
    {
        // Convert the domain model (Book) to a JPA entity (BookEntity)
        BookEntity entity = bookEntityMapper.toEntity(book);

        // Prepare a list to hold managed AuthorEntity instances


        // Persist the BookEntity and return the saved Book as a domain model
        BookEntity saved = bookRepo.save(entity);

        return bookEntityMapper.toModel(saved);
    }

    @Override
    public void delete(Book book)
    {
        // TODO: implement delete logic
    }
}


