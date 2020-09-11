package kms;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class IntervalRateLimiter implements RateLimiter {

    private final Long intervalSec;
    private final Integer rate;
    private volatile Long start = Instant.now().getEpochSecond();
    private final AtomicInteger counter = new AtomicInteger(0);

    public IntervalRateLimiter(Duration interval, Integer rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Rate must be great than 0");
        }
        this.intervalSec = interval.getSeconds();
        this.rate = rate;
    }

    public boolean check() {
        long now = Instant.now().getEpochSecond();
        if (now - start <= intervalSec ) {
            return counter.incrementAndGet() <= rate;
        } else {
            counter.set(1);
            start = now;
            return true;
        }
    }
}
