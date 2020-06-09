package org.example.core.basic;

import java.util.concurrent.ExecutorService;

/**
 * @author lcb
 * @date 2020/5/15
 */
public interface BasicRateLimiter {
    boolean acquire();
    void addToken(final ExecutorService executorService);
}
