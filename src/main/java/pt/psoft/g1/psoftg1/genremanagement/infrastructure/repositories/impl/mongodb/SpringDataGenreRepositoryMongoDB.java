package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataGenreRepositoryMongoDB extends MongoRepository<GenreMongoDB, String> {
    @Query("{}")
    List<GenreMongoDB> findAllGenres();

    @Query("{ 'genre': ?0 }")
    Optional<GenreMongoDB> findByString(String genre);

    @Aggregation(pipeline = {
            "{ $lookup: { from: 'books', localField: '_id', foreignField: 'genre.genreId', as: 'books' } }",
            "{ $unwind: { path: '$books', preserveNullAndEmptyArrays: false } }",
            "{ $group: { _id: '$genre', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 5 }",
            "{ $project: { _id: 0, genre: '$_id', bookCount: '$count' } }"
    })
    Page<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { startDate: { $gte: ?0, $lte: ?1 } } }",
            "{ $lookup: { from: 'books', localField: 'bookId', foreignField: '_id', as: 'book' } }",
            "{ $unwind: '$book' }",
            "{ $lookup: { from: 'genres', localField: 'book.genreId', foreignField: '_id', as: 'genre' } }",
            "{ $unwind: '$genre' }",
            "{ $project: { genreName: '$genre.genre', year: { $year: '$startDate' }, month: { $month: '$startDate' } } }",
            "{ $group: { _id: { genre: '$genreName', year: '$year', month: '$month' }, count: { $sum: 1 } } }",
            "{ $project: { _id: 0, genre: '$_id.genre', year: '$_id.year', month: '$_id.month', count: 1 } }",
            "{ $sort: { year: 1, month: 1, genre: 1 } }"
    })
    List<GenreLendingsDTO> getLendingsPerMonthByGenre(LocalDate startDate, LocalDate endDate);

    @Aggregation(pipeline = {
            "{ $match: { startDate: { $gte: ?0, $lte: ?1 } } }",
            "{ $lookup: { from: 'books', localField: 'bookId', foreignField: '_id', as: 'book' } }",
            "{ $unwind: '$book' }",
            "{ $lookup: { from: 'genres', localField: 'book.genreId', foreignField: '_id', as: 'genre' } }",
            "{ $unwind: '$genre' }",
            "{ $group: { _id: '$genre.genre', loanCount: { $sum: 1 } } }",
            "{ $project: { genre: '$_id', dailyAvg: { $divide: ['$loanCount', ?2] } } }"
    })
    List<GenreLendingsDTO> getAverageLendingsInMonth(LocalDate firstOfMonth, LocalDate lastOfMonth, int daysInMonth);

    @Aggregation(pipeline = {
            "{ $match: { startDate: { $gte: ?0, $lte: ?1 }, returnedDate: { $ne: null } } }",
            "{ $lookup: { from: 'books', localField: 'bookId', foreignField: '_id', as: 'book' } }",
            "{ $unwind: '$book' }",
            "{ $lookup: { from: 'genres', localField: 'book.genreId', foreignField: '_id', as: 'genre' } }",
            "{ $unwind: '$genre' }",
            "{ $project: { genreName: '$genre.genre', year: { $year: '$startDate' }, month: { $month: '$startDate' }, durationInDays: { $divide: [ { $subtract: ['$returnedDate', '$startDate'] }, 1000*60*60*24 ] } } }",
            "{ $group: { _id: { genre: '$genreName', year: '$year', month: '$month' }, avgDuration: { $avg: '$durationInDays' } } }",
            "{ $project: { _id: 0, genre: '$_id.genre', year: '$_id.year', month: '$_id.month', avgDuration: 1 } }",
            "{ $sort: { year: 1, month: 1, genre: 1 } }"
    })
    List<GenreLendingsDTO> getLendingsAverageDurationPerMonth(LocalDate startDate, LocalDate endDate);
}