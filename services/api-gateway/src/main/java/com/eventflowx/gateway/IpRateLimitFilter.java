package com.eventflowx.gateway;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class IpRateLimitFilter implements GlobalFilter, Ordered {

    private static final long WINDOW_MILLIS = 60_000L;
    private static final long STALE_WINDOW_MILLIS = 5 * WINDOW_MILLIS;

    private final Map<String, CounterWindow> counters = new ConcurrentHashMap<>();
    private final AtomicLong cleanupTicker = new AtomicLong();

    @Value("${eventflowx.gateway.rate-limit.enabled:true}")
    private boolean enabled;

    @Value("${eventflowx.gateway.rate-limit.requests-per-minute:120}")
    private int requestsPerMinute;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!enabled) {
            return chain.filter(exchange);
        }

        long now = System.currentTimeMillis();
        maybeCleanup(now);

        String key = resolveClientIp(exchange);
        CounterWindow window = counters.computeIfAbsent(key, k -> new CounterWindow(now));

        int currentCount;
        synchronized (window) {
            if (now - window.windowStartMillis >= WINDOW_MILLIS) {
                window.windowStartMillis = now;
                window.counter.set(0);
            }
            currentCount = window.counter.incrementAndGet();
        }

        if (currentCount > requestsPerMinute) {
            return writeRateLimitResponse(exchange);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private String resolveClientIp(ServerWebExchange exchange) {
        String forwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        InetSocketAddress remote = exchange.getRequest().getRemoteAddress();
        return remote == null ? "unknown" : remote.getAddress().getHostAddress();
    }

    private void maybeCleanup(long now) {
        if (cleanupTicker.incrementAndGet() % 200 != 0) {
            return;
        }
        counters.entrySet().removeIf(entry -> now - entry.getValue().windowStartMillis > STALE_WINDOW_MILLIS);
    }

    private Mono<Void> writeRateLimitResponse(ServerWebExchange exchange) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String path = exchange.getRequest().getPath().value();
        String body = "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded\",\"path\":\""
                + path + "\",\"timestamp\":\"" + Instant.now() + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    private static final class CounterWindow {
        private long windowStartMillis;
        private final AtomicInteger counter = new AtomicInteger(0);

        private CounterWindow(long windowStartMillis) {
            this.windowStartMillis = windowStartMillis;
        }
    }
}
