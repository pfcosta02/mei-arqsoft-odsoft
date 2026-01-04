package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookTempEntity;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookTempRepository;

import java.util.Optional;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class BookTempRepositoryImpl implements BookTempRepository {

    private final SpringDataBookTempRepository bookTempRepository;

    @Override
    public BookTempEntity save(BookTempEntity temp) {
        return bookTempRepository.save(temp);
    }

    @Override
    public void delete(BookTempEntity temp) {
        bookTempRepository.delete(temp);
    }

    @Override
    public Optional<BookTempEntity> findByIsbn(String isbn) {
        return bookTempRepository.findByIsbn(isbn);
    }
}
