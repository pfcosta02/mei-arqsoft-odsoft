package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb;

import com.mongodb.client.MongoClient;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.redis.GenreRepositoryRedisImpl;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreMapperMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;

import java.time.LocalDate;
import java.util.*;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@Repository
@RequiredArgsConstructor
public class GenreRepositoryMongoDBImpl implements GenreRepository {

    private final SpringDataGenreRepositoryMongoDB genreRepo;
    private final GenreMapperMongoDB genreEntityMapper;
    private final GenreRepositoryRedisImpl redisRepo;

    private MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;
    private final MongoClient mongo;

    private static final String PREFIX = "genres:";

    @Override
    public Iterable<Genre> findAll()
    {
        List<Genre> genres = new ArrayList<>();
        for (GenreMongoDB g: genreRepo.findAll())
        {
            genres.add(genreEntityMapper.toModel(g));
        }

        return genres;
    }

    @Override
    public Optional<Genre> findByString(String genreName)
    {
        Optional<Genre> cached = redisRepo.getGenreFromRedis(PREFIX + "genre:" + genreName);
        if (cached.isPresent()) return cached;

        Optional<GenreMongoDB> entityOpt = genreRepo.findByString(genreName);
        if (entityOpt.isPresent())
        {
            redisRepo.save(genreEntityMapper.toModel(entityOpt.get()));
            return Optional.of(genreEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Genre save(Genre genre)
    {
        GenreMongoDB saved = genreRepo.save(genreEntityMapper.toMongoDB(genre));
        redisRepo.save(genreEntityMapper.toModel(saved));
        return genreEntityMapper.toModel(saved);
    }

    @Override
    public List<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable)
    {
        List<GenreBookCountDTO> cached = redisRepo.getGenreBookCountListFromRedis(PREFIX + "top5");
        if (!cached.isEmpty()) return cached;

        List<GenreBookCountDTO> genreBookCountDTOS = genreRepo.findTop5GenreByBookCount(pageable);
        redisRepo.cacheGenreBookCountListToRedis(PREFIX + "top5", genreBookCountDTOS);
        return genreRepo.findTop5GenreByBookCount(pageable);
    }

    @Override
    public void delete(Genre genre)
    {
        genreRepo.delete(genreEntityMapper.toMongoDB(genre));
    }

    @Override
    public List<GenreLendingsPerMonthDTO> getLendingsPerMonthLastYearByGenre() {
        LocalDate now = LocalDate.now();
        LocalDate twelveMonthsAgo = now.minusMonths(12);

        List<GenreLendingsDTO> rawResults = genreRepo.getLendingsPerMonthByGenre(twelveMonthsAgo, now);

        // Agrupa por ano e mês como antes
        Map<Integer, Map<Integer, List<GenreLendingsDTO>>> groupedResults = new HashMap<>();

        for (GenreLendingsDTO dto : rawResults) {
            groupedResults
                    .computeIfAbsent(dto.getYear(), k -> new HashMap<>())
                    .computeIfAbsent(dto.getMonth(), k -> new ArrayList<>())
                    .add(new GenreLendingsDTO(dto.getGenre(), dto.getCount()));
        }

        return getGenreLendingsPerMonthDtos(groupedResults);
    }

    @Override
    public List<GenreLendingsDTO> getAverageLendingsInMonth(LocalDate month, pt.psoft.g1.psoftg1.shared.services.Page page) {
        int daysInMonth = month.lengthOfMonth();
        LocalDate firstOfMonth = month.withDayOfMonth(1);
        LocalDate lastOfMonth = month.withDayOfMonth(daysInMonth);

        List<GenreLendingsDTO> results =
                genreRepo.getAverageLendingsInMonth(firstOfMonth, lastOfMonth, daysInMonth);

        int fromIndex = (page.getNumber() - 1) * page.getLimit();
        int toIndex = Math.min(fromIndex + page.getLimit(), results.size());

        if (fromIndex >= results.size()) {
            return Collections.emptyList();
        }

        return results.subList(fromIndex, toIndex);
    }

    @Override
    public List<GenreLendingsPerMonthDTO> getLendingsAverageDurationPerMonth(LocalDate startDate, LocalDate endDate) {
        List<GenreLendingsDTO> rawResults =
                genreRepo.getLendingsAverageDurationPerMonth(startDate, endDate);

        Map<Integer, Map<Integer, List<GenreLendingsDTO>>> groupedResults = new HashMap<>();

        for (GenreLendingsDTO dto : rawResults) {
            int year = dto.getYear();
            int month = dto.getMonth();
            double avgDuration = dto.getValue().doubleValue();

            GenreLendingsDTO genreLendingsDTO = new GenreLendingsDTO(dto.getGenre(), avgDuration);

            groupedResults
                    .computeIfAbsent(year, y -> new HashMap<>())
                    .computeIfAbsent(month, m -> new ArrayList<>())
                    .add(genreLendingsDTO);
        }

        return getGenreLendingsPerMonthDtos(groupedResults);
    }

    @NotNull
    private List<GenreLendingsPerMonthDTO> getGenreLendingsPerMonthDtos(Map<Integer, Map<Integer, List<GenreLendingsDTO>>> groupedResults)
    {
        List<GenreLendingsPerMonthDTO> lendingsPerMonth = new ArrayList<>();
        for (Map.Entry<Integer, Map<Integer, List<GenreLendingsDTO>>> yearEntry : groupedResults.entrySet()) {
            int yearValue = yearEntry.getKey();
            for (Map.Entry<Integer, List<GenreLendingsDTO>> monthEntry : yearEntry.getValue().entrySet()) {
                int monthValue = monthEntry.getKey();
                List<GenreLendingsDTO> values = monthEntry.getValue();
                lendingsPerMonth.add(new GenreLendingsPerMonthDTO(yearValue, monthValue, values));
            }
        }

        return lendingsPerMonth;
    }
}