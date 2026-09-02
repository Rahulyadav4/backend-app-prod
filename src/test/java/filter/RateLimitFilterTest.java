package filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.taskmanager.filter.RateLimitFilter;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.BucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.distributed.proxy.RemoteBucketBuilder;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class RateLimitFilterTest {

    private void setField(
            Object object,
            String fieldName,
            Object value) throws Exception {

        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }


    @Test
    void testRequestAllowed() throws Exception {

        @SuppressWarnings("unchecked")
        ProxyManager<byte[]> proxyManager =
                mock(ProxyManager.class);

        @SuppressWarnings("unchecked")
        RemoteBucketBuilder<byte[]> builder =
                mock(RemoteBucketBuilder.class);

        BucketProxy bucket =
                mock(BucketProxy.class);

        when(proxyManager.builder())
                .thenReturn(builder);


        /*
         * IMPORTANT:
         *
         * RateLimitFilter calls:
         *
         * builder.build(key, configSupplier)
         *
         * Bucket4j has overloaded build() methods.
         *
         * The explicit Supplier matcher makes Mockito
         * select the correct overload.
         */
        doAnswer(invocation -> {

            @SuppressWarnings("unchecked")
            Supplier<BucketConfiguration> supplier =
                    (Supplier<BucketConfiguration>)
                            invocation.getArgument(1);

            /*
             * Execute the real configuration supplier.
             * This covers the BucketConfiguration code.
             */
            BucketConfiguration configuration =
                    supplier.get();

            Assertions.assertNotNull(configuration);

            return bucket;

        }).when(builder).build(
                any(byte[].class),
                org.mockito.ArgumentMatchers
                        .<Supplier<BucketConfiguration>>any()
        );


        when(bucket.tryConsume(1))
                .thenReturn(true);


        RateLimitFilter filter =
                new RateLimitFilter(proxyManager);


        /*
         * @Value does not run when using "new".
         * Set valid values manually for the unit test.
         */
        setField(filter, "capacity", 100);
        setField(filter, "refillPerMinute", 100);


        HttpServletRequest request =
                mock(HttpServletRequest.class);

        HttpServletResponse response =
                mock(HttpServletResponse.class);

        FilterChain filterChain =
                mock(FilterChain.class);


        when(request.getRemoteAddr())
                .thenReturn("127.0.0.1");


        filter.doFilterInternal(
                request,
                response,
                filterChain
        );


        verify(bucket, times(1))
                .tryConsume(1);

        verify(filterChain, times(1))
                .doFilter(request, response);

        verify(response, never())
                .setStatus(429);
    }


    @Test
    void testRequestRejected() throws Exception {

        @SuppressWarnings("unchecked")
        ProxyManager<byte[]> proxyManager =
                mock(ProxyManager.class);

        @SuppressWarnings("unchecked")
        RemoteBucketBuilder<byte[]> builder =
                mock(RemoteBucketBuilder.class);

        BucketProxy bucket =
                mock(BucketProxy.class);

        when(proxyManager.builder())
                .thenReturn(builder);


        /*
         * Explicitly select:
         *
         * build(byte[], Supplier<BucketConfiguration>)
         */
        doAnswer(invocation -> {

            @SuppressWarnings("unchecked")
            Supplier<BucketConfiguration> supplier =
                    (Supplier<BucketConfiguration>)
                            invocation.getArgument(1);

            /*
             * Execute the supplier so the production
             * BucketConfiguration code is actually covered.
             */
            BucketConfiguration configuration =
                    supplier.get();

            Assertions.assertNotNull(configuration);

            return bucket;

        }).when(builder).build(
                any(byte[].class),
                org.mockito.ArgumentMatchers
                        .<Supplier<BucketConfiguration>>any()
        );


        /*
         * Simulate rate limit exceeded.
         */
        when(bucket.tryConsume(1))
                .thenReturn(false);


        RateLimitFilter filter =
                new RateLimitFilter(proxyManager);


        /*
         * Prevent the previous:
         *
         * "tokens should be positive"
         *
         * error.
         */
        setField(filter, "capacity", 100);
        setField(filter, "refillPerMinute", 100);


        HttpServletRequest request =
                mock(HttpServletRequest.class);

        HttpServletResponse response =
                mock(HttpServletResponse.class);

        FilterChain filterChain =
                mock(FilterChain.class);


        when(request.getRemoteAddr())
                .thenReturn("127.0.0.1");


        StringWriter stringWriter =
                new StringWriter();

        PrintWriter printWriter =
                new PrintWriter(stringWriter);


        when(response.getWriter())
                .thenReturn(printWriter);


        filter.doFilterInternal(
                request,
                response,
                filterChain
        );


        verify(bucket, times(1))
                .tryConsume(1);


        verify(response, times(1))
                .setStatus(429);


        verify(response, times(1))
                .setContentType("text/plain");


        verify(response, times(1))
                .getWriter();


        verify(filterChain, never())
                .doFilter(request, response);


        printWriter.flush();


        String responseBody =
                stringWriter.toString();


        Assertions.assertTrue(
                responseBody.contains("Rate limit exceeded")
        );
    }
}