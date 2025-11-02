package pt.psoft.g1.psoftg1.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component // Only runs when 'redis' profile is active
//@Profile("!es")
@Order(1) // Executes first
public class RedisClean implements CommandLineRunner {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisClean(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(String... args) {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
        System.out.println("🧹 Redis cache cleared before bootstrap.");
    }
}
