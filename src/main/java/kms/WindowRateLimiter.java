package kms;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class WindowRateLimiter implements RateLimiter {

    private final Long windowSec;
    private final Integer rate;
    private final List<Long> history = new LinkedList<>();

    public WindowRateLimiter(Duration window, Integer rate) {
        if (rate <= 0) {
            throw new IllegalArgumentException("Rate must be great than 0");
        }
        this.windowSec = window.getSeconds();
        this.rate = rate;
    }

    @Override
    synchronized public boolean check() {
        long now = Instant.now().getEpochSecond();
        history.removeIf( item -> item < now - windowSec);
        if (history.size() < rate) {
            history.add(now);
            return true;
        } else {
            return false;
        }
    }
}
