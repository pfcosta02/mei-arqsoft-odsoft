package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class RedisCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;
    private final Duration defaultTtl;

    public RedisCacheRepository(RedisTemplate<String, Object> redisTemplate,
                                ObjectMapper redisObjectMapper,
                                @Value("${spring.cache.redis.time-to-live}") Duration defaultTtl) {
        this.redisTemplate = redisTemplate;
        this.mapper = redisObjectMapper;
        this.defaultTtl = defaultTtl;
    }

    public <T> void save(String key, T value) {
        redisTemplate.opsForValue().set(key, value, defaultTtl);
    }

    public void saveEmpty(String key) {
        redisTemplate.opsForValue().set(key, "NULL", defaultTtl);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) return Optional.empty();
        if ("NULL".equals(value)) return Optional.empty();
        return Optional.of((T) value);
    }

    public <T> Optional<List<T>> findList(String key, Class<T> elementType) {
        Object cachedValue = redisTemplate.opsForValue().get(key);
        if (cachedValue == null) return Optional.empty();
        if ("NULL".equals(cachedValue)) return Optional.empty();

        // Convert generic LinkedHashMaps to the correct type
        List<T> list = mapper.convertValue(
                cachedValue,
                mapper.getTypeFactory().constructCollectionType(List.class, elementType)
        );
        return Optional.of(list);
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }

    public void evictPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
