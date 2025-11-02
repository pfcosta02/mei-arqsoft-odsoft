package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorRedisMapper;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.redis.AuthorRedisDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class AuthorRepositoryRedisImpl {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper mapper;

    private final AuthorRedisMapper redisMapper;

    private static final String PREFIX = "authors:";

    public List<Author> getAuthorListFromRedis(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                List<AuthorRedisDTO> dtoList = mapper.readValue(json, new TypeReference<List<AuthorRedisDTO>>() {});
                return dtoList.stream()
                        .map(redisMapper::toDomain)
                        .toList();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize authors from Redis", e);
            }
        }
        return List.of();
    }

    public void cacheAuthorListToRedis(String key, List<Author> authors) {
        List<AuthorRedisDTO> dtoList = authors.stream()
                .map(redisMapper::toDTO)
                .toList();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(dtoList);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize AuthorRedisDTO list", e);
        }
    }

    public Optional<Author> getAuthorFromRedis(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null ? Optional.of(redisMapper.toDomain((AuthorRedisDTO) obj)) : Optional.empty();
    }

    public void save(Author author) {
        AuthorRedisDTO dto = redisMapper.toDTO(author);
        redisTemplate.opsForValue().set(PREFIX + "author:" + author.getAuthorNumber(), dto);
        invalidateCacheForAuthor();
    }

    public void delete(Author author) {
        redisTemplate.delete(PREFIX + author.getAuthorNumber());
    }


    private void invalidateCacheForAuthor() {
        deleteKeysByPattern("authors:*");
    }

    private void deleteKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
