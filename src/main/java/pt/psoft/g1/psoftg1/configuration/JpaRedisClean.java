package pt.psoft.g1.psoftg1.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Profile("jpa")
@Component
@Order(1)
public class JpaRedisClean implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    public JpaRedisClean(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        // Delete all keys
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        System.out.println("✅ Redis cache cleared on startup.");
    }
}