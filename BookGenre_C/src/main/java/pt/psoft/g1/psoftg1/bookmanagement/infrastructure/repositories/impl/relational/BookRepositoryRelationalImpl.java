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
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational.AuthorRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookEntityMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.services.SearchBooksQuery;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational.GenreRepositoryRelationalImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;

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
    private final GenreRepositoryRelationalImpl genreRepo;
    private final AuthorRepositoryRelationalImpl authorRepo;
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

        // Retrieve the existing Genre model from the repository
        // Throws an exception if the genre is not found
        Genre genreModel = genreRepo.findByString(book.getGenre().getGenre())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        // Get the managed JPA reference for the GenreEntity using its database ID (pk)
        // This ensures we use the existing GenreEntity instead of creating a new one
        GenreEntity genreEntity = em.getReference(GenreEntity.class, genreModel.getPk());

        // Set the managed GenreEntity on the BookEntity
        entity.setGenre(genreEntity);

        // Prepare a list to hold managed AuthorEntity instances
        List<AuthorEntity> authors = new ArrayList<>();

        // For each author in the Book model
        for (var author : book.getAuthors())
        {
            // Retrieve the corresponding Author model from the repository by author number
            //TODO: temos aqui uma questao, o searchByNameName retorna uma lista de nomes, entao pode nao ser o autor correto (no caso de haver varios autores com o mesmo nome)
            Author auth  = authorRepo.searchByNameName(author.getName().getName()).get(0);
            if (auth == null)
            {
                throw new RuntimeException("Author not found");
            }

            // Get a managed reference to the existing AuthorEntity by its author number
            AuthorEntity authorEntity = em.getReference(AuthorEntity.class, auth.getAuthorNumber());

            // Add the managed AuthorEntity to the list
            authors.add(authorEntity);
        }

        // Associate all managed AuthorEntity objects with the BookEntity
        entity.setAuthors(authors);

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


