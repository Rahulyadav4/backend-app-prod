package com.taskmanager.config;

import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

// FIX (scaling bug): bucket4j-core alone has no shared backing store, so each
// pod enforced its own independent limit — real global throughput was
// configured_limit x live_replica_count, silently growing as HPA scaled out.
@Configuration
public class RateLimiterConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient() {
        return RedisClient.create("redis://" + redisHost + ":" + redisPort);
    }

    @Bean
    public ProxyManager<byte[]> bucketProxyManager(RedisClient redisClient) {
        StatefulRedisConnection<byte[], byte[]> connection =
                redisClient.connect(ByteArrayCodec.INSTANCE);

        return LettuceBasedProxyManager.builderFor(connection)
                .withExpirationStrategy(
                        ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(Duration.ofMinutes(2)))
                .build();
    }
}