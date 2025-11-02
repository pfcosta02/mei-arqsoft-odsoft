package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.mappers.BookRedisMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.redis.BookRedisDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BookRepositoryRedisImpl {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper mapper;

    private final BookRedisMapper redisMapper;

    private static final String PREFIX = "books:";

    public List<Book> getBookListFromRedis(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                List<BookRedisDTO> dtoList = mapper.readValue(json, new TypeReference<List<BookRedisDTO>>() {});
                return dtoList.stream()
                        .map(redisMapper::toDomain)
                        .toList();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize books from Redis", e);
            }
        }
        return List.of();
    }

    public void cacheBookListToRedis(String key, List<Book> books) {
        List<BookRedisDTO> dtoList = books.stream()
                .map(redisMapper::toDTO)
                .toList();
        try {
            String json = mapper.writeValueAsString(dtoList);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BookRedisDTO list", e);
        }
    }

    public Optional<Book> getBookFromRedis(String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null ? Optional.of(redisMapper.toDomain((BookRedisDTO) obj)) : Optional.empty();
    }

    public void save(Book book) {
        BookRedisDTO dto = redisMapper.toDTO(book);
        redisTemplate.opsForValue().set(PREFIX + "isbn:" + book.getIsbn().toString(), dto);
        invalidateCacheForBook();
    }

    public void delete(Book book) {
        redisTemplate.delete(PREFIX + book.getIsbn().toString());
    }


    private void invalidateCacheForBook() {
        deleteKeysByPattern(PREFIX + "genre:*");
        deleteKeysByPattern(PREFIX + "title:*");
        deleteKeysByPattern(PREFIX + "author:*");
    }

    private void deleteKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }


}
