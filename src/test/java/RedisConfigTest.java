

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import com.taskmanager.configr.RedisConfig;

class RedisConfigTest {

    @Test
    void redisConnectionFactory_shouldCreateFactory() {

        RedisConfig config = new RedisConfig();

        RedisConnectionFactory factory =
                config.redisConnectionFactory();

        assertNotNull(factory);
    }

    @Test
    void cacheManager_shouldCreateCacheManager() {

        RedisConfig config = new RedisConfig();

        LettuceConnectionFactory factory =
                config.redisConnectionFactory();

        RedisCacheManager cacheManager =
                config.cacheManager(factory);

        assertNotNull(cacheManager);
    }
}