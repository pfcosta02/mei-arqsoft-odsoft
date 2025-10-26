package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pt.psoft.g1.psoftg1.readermanagement.model.mongodb.ReaderDetailsMongoDB;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataReaderRepositoryMongoDB extends MongoRepository<ReaderDetailsMongoDB, String> {

    @Query("{ 'readerNumber.reader_number': ?0 }")
    Optional<ReaderDetailsMongoDB> findByReaderNumber(String readerNumber);


    @Query("{ 'phoneNumber': ?0 }")
    List<ReaderDetailsMongoDB> findByPhoneNumber(String phoneNumber);


    @Query("{ 'user.username': ?0 }")
    Optional<ReaderDetailsMongoDB> findByUsername(String username);


    @Query("{ 'user.id': ?0 }")
    Optional<ReaderDetailsMongoDB> findByUserId(String userId);


    @Aggregation(pipeline = {
            "{ '$lookup': { 'from': 'user', 'localField': 'reader.id', 'foreignField': '_id', 'as': 'user' }}",
            "{ '$unwind': '$user' }",
            "{ '$match': { '$expr': { '$eq': [ { '$year': '$user.createdAt' }, { '$year': '$$NOW' }] }}}",
            "{ '$count': 'count' }"
    })
    int getCountFromCurrentYear();


    @Aggregation(pipeline = {
            "{ $lookup: { from: 'lendings', localField: '_id', foreignField: 'reader.readerId', as: 'lendings' } }",
            "{ $addFields: { lendingCount: { $size: '$lendings' } } }",
            "{ $sort: { lendingCount: -1 } }",
            "{ $limit: 10 }"
    })
    List<ReaderDetailsMongoDB> findTopReaders(Pageable pageable);

    @Aggregation(pipeline = {
            // Unir Reader -> Lending
            "{ $lookup: { from: 'lendings', localField: '_id', foreignField: 'reader.readerId', as: 'lendings' } }",
            "{ $unwind: '$lendings' }",

            // Unir Lending -> Book
            "{ $lookup: { from: 'books', localField: 'lendings.book.bookId', foreignField: '_id', as: 'book' } }",
            "{ $unwind: '$book' }",

            // Filtrar por género e datas
            "{ $match: { 'book.genre': ?0, 'lendings.startDate': { $gte: ?1, $lte: ?2 } } }",

            // Agrupar por leitor
            "{ $group: { _id: '$_id', reader: { $first: '$$ROOT' }, count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }",
            "{ $limit: 10 }",

            // Projetar apenas os dados relevantes
            "{ $project: { reader: 1, count: 1, _id: 0 } }"
    })
    List<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate);

    @Query("{}")
    List<ReaderDetailsMongoDB> findAll();
}