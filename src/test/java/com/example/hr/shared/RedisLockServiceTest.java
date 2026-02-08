package com.example.hr.shared;

import com.example.hr.shared.infra.RedisLockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class RedisLockServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7").withExposedPorts(6379);

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private RedisLockService lockService;

    @Test
    void acquiresAndReleasesLock() {
        String key = "lock:test";
        assertThat(lockService.acquireLock(key, Duration.ofSeconds(5))).isTrue();
        assertThat(lockService.acquireLock(key, Duration.ofSeconds(5))).isFalse();
        lockService.releaseLock(key);
        assertThat(lockService.acquireLock(key, Duration.ofSeconds(5))).isTrue();
    }
}
