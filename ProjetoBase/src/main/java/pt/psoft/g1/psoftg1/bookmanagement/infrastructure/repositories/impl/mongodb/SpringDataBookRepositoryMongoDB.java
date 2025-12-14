package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.BookMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.model.mongodb.IsbnMongoDB;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataBookRepositoryMongoDB extends MongoRepository<BookMongoDB, String> {

    @Query("{ 'isbn.isbn': ?0 }")
    Optional<BookMongoDB> findByIsbn(String isbn);

    @Query("{ 'genre.genre': { $regex: ?0, $options: 'i' } }") // Case-insensitive regex search
    List<BookMongoDB> findByGenre(String genre);

    @Query("{ 'title.title': { $regex: ?0, $options: 'i' } }") // Case-insensitive regex search
    List<BookMongoDB> findByTitle(String title);

    @Query(value = "{ 'authors.name.name': { $regex: ?0, $options: 'i' } }") // Case-insensitive regex search
    List<BookMongoDB> findByAuthorName(String authorName);

    @Query(value = "{ 'authors.authorNumber': ?0 }")
    List<BookMongoDB> findBooksByAuthorNumber(String authorNumber);

    @Aggregation(pipeline = {
            "{ $match: { 'startDate': { $gt: ?0 } } }",
            "{ $group: { _id: '$bookId', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 5 }",
            "{ $lookup: { from: 'books', localField: '_id', foreignField: 'bookId', as: 'book' } }",
            "{ $unwind: '$book' }",
            "{ $project: { book: 1, count: 1 } }"
    })
    Page<BookCountDTO> findTop5BooksLent(LocalDate oneYearAgo, Pageable pageable);
}
