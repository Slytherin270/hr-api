package com.example.hr.shared.domain;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class TimeProvider {
    private final Clock clock;

    public TimeProvider(Clock clock) {
        this.clock = clock;
    }

    public Instant now() {
        return Instant.now(clock);
    }

    public static TimeProvider systemUtc() {
        return new TimeProvider(Clock.systemUTC());
    }

    public static Clock utcClock() {
        return Clock.system(ZoneOffset.UTC);
    }
}
