package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreRedisMapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.redis.GenreRedisDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class GenreRepositoryRedisImpl {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper mapper;

    private final GenreRedisMapper redisMapper;

    private static final String PREFIX = "genres:";

    public List<Genre> getGenreListFromRedis(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                List<GenreRedisDTO> dtoList = mapper.readValue(json, new TypeReference<List<GenreRedisDTO>>() {});
                return dtoList.stream()
                        .map(redisMapper::toDomain)
                        .toList();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize genres from Redis", e);
            }
        }
        return List.of();
    }

    public void cacheGenreListToRedis(String key, List<Genre> genres) {
        List<GenreRedisDTO> dtoList = genres.stream()
                .map(redisMapper::toDTO)
                .toList();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(dtoList);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize GenreRedisDTO list", e);
        }
    }

    public List<GenreBookCountDTO> getGenreBookCountListFromRedis(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                List<GenreBookCountDTO> dtoList = mapper.readValue(json, new TypeReference<List<GenreBookCountDTO>>() {});
                return dtoList.stream().toList();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize genres from Redis", e);
            }
        }
        return List.of();
    }

    public void cacheGenreBookCountListToRedis(String key, List<GenreBookCountDTO> genreBookCountDTOS) {
        List<GenreBookCountDTO> dtoList = genreBookCountDTOS.stream().toList();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(dtoList);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize GenreRedisDTO list", e);
        }
    }

    public Optional<Genre> getGenreFromRedis(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null ? Optional.of(redisMapper.toDomain((GenreRedisDTO) obj)) : Optional.empty();
    }

    public void save(Genre genre) {
        GenreRedisDTO dto = redisMapper.toDTO(genre);
        redisTemplate.opsForValue().set(PREFIX + "genre:" + genre.getGenre(), dto);
        invalidateCacheForGenre();
    }

    private void invalidateCacheForGenre() {
        deleteKeysByPattern(PREFIX + "*");
    }

    private void deleteKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
