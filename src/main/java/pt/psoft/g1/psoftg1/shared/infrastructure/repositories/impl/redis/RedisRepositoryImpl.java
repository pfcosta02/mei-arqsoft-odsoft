package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.redis;

import org.springframework.data.redis.core.RedisTemplate;
import pt.psoft.g1.psoftg1.shared.repositories.CacheRepository;

import java.util.Optional;

public class RedisRepositoryImpl <T, K> implements CacheRepository<T, K> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Class<T> type;

    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate, Class<T> type) {
        this.redisTemplate = redisTemplate;
        this.type = type;
    }

    private String key(K id) {
        return type.getSimpleName() + ":" + id;
    }

    @Override
    public Optional<T> findById(K id) {
        T entity = (T) redisTemplate.opsForValue().get(key(id));
        return Optional.ofNullable(entity);
    }

    @Override
    public void save(T entity, K id) {
        redisTemplate.opsForValue().set(key(id), entity);
    }

    @Override
    public void delete(K id) {
        redisTemplate.delete(key(id));
    }
}
