package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mongodb;

import com.mongodb.client.MongoClient;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
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

    private MongoTemplate mongoTemplate;
    private final MongoClient mongoClient;
    private final MongoClient mongo;


    @Override
    @Cacheable(value = "genres", key = "'all'")
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
    @Cacheable(value = "genres", key = "#genreName")
    public Optional<Genre> findByString(String genreName)
    {
        Optional<GenreMongoDB> entityOpt = genreRepo.findByString(genreName);
        if (entityOpt.isPresent())
        {
            return Optional.of(genreEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "genres", key = "#genre.genre"),
            @CacheEvict(cacheNames = "genres", allEntries = true, condition = "#genre.pk == null"),
            @CacheEvict(cacheNames = "genres_top5", allEntries = true),
            @CacheEvict(cacheNames = {
                    "genres_lendings_per_month_last_year",
                    "genres_lendings_avg_duration",
                    "genres_lendings_avg_in_month"
            }, allEntries = true)
    })
    public Genre save(Genre genre)
    {
        return genreEntityMapper.toModel(genreRepo.save(genreEntityMapper.toMongoDB(genre)));
    }

    @Override
    @Cacheable(value = "genres_top5", key = "#pageable.pageNumber")
    public List<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable)
    {
        return genreRepo.findTop5GenreByBookCount(pageable);
    }

    @Override
    public void delete(Genre genre)
    {
        genreRepo.delete(genreEntityMapper.toMongoDB(genre));
    }

    @Override
    @Cacheable(value = "genres_lendings_per_month_last_year")
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
    @Cacheable(value = "genres_lendings_avg_in_month", key = "#month")
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
    @Cacheable(value = "genres_lendings_avg_duration", key = "{#startDate, #endDate}")
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