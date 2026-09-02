package com.taskmanager.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

class RateLimiterConfigTest {

    @Test
    void redisClient_shouldCreateRedisClient() throws Exception {

        RateLimiterConfig config = new RateLimiterConfig();

        Field hostField =
                RateLimiterConfig.class.getDeclaredField("redisHost");
        hostField.setAccessible(true);
        hostField.set(config, "localhost");

        Field portField =
                RateLimiterConfig.class.getDeclaredField("redisPort");
        portField.setAccessible(true);
        portField.set(config, 6379);

        RedisClient redisClient = config.redisClient();

        assertNotNull(redisClient);

        redisClient.shutdown();
    }

    @Test
    @SuppressWarnings("unchecked")
    void bucketProxyManager_shouldCreateProxyManager() throws Exception {

        RateLimiterConfig config = new RateLimiterConfig();

        Field hostField =
                RateLimiterConfig.class.getDeclaredField("redisHost");
        hostField.setAccessible(true);
        hostField.set(config, "localhost");

        Field portField =
                RateLimiterConfig.class.getDeclaredField("redisPort");
        portField.setAccessible(true);
        portField.set(config, 6379);

        RedisClient redisClient = mock(RedisClient.class);

        StatefulRedisConnection<byte[], byte[]> connection =
                mock(StatefulRedisConnection.class);

        RedisAsyncCommands<byte[], byte[]> asyncCommands =
                mock(RedisAsyncCommands.class);

        when(redisClient.connect(ByteArrayCodec.INSTANCE))
                .thenReturn(connection);

        when(connection.async())
                .thenReturn(asyncCommands);

        ProxyManager<byte[]> proxyManager =
                config.bucketProxyManager(redisClient);

        assertNotNull(proxyManager);
    }
}