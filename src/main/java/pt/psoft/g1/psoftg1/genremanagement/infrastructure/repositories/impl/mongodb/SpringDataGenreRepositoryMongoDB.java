package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataGenreRepositoryMongoDB extends MongoRepository<GenreMongoDB, Integer> {
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
}
