package kms;

import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WindowLimiterTest {

    @Test
    public void limitRates() throws InterruptedException {
        RateLimiter limiter = new WindowRateLimiter(Duration.ofSeconds(1), 2);
        Assert.assertTrue(limiter.check());
        Thread.sleep(100L);
        Assert.assertTrue(limiter.check());
        Thread.sleep(100L);
        Assert.assertFalse(limiter.check());
    }

    @Test
    public void limiterForFewThreads() throws InterruptedException, ExecutionException {
        int nThreads = 5;
        List<Future<Boolean>> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        RateLimiter limiter = new WindowRateLimiter(Duration.ofSeconds(1), 2);
        for (int i = 0; i < nThreads; ++i) {
            results.add(executorService.submit(limiter::check));
        }
        Thread.sleep(2000L);

        int allowed = 0;
        int denied = 0;
        for (int i = 0; i < nThreads; ++i) {
            if (results.get(i).get()) {
                allowed++;
            } else {
                denied++;
            }
        }

        Assert.assertEquals(2, allowed);
        Assert.assertEquals(3, denied);
    }
}
