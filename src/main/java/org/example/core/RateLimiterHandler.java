package org.example.core;

import org.example.core.basic.BasicRateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lcb
 * @date 2020/6/2
 */
public class RateLimiterHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiterHandler.class);
    private static final Map<String, BasicRateLimiter> map = new ConcurrentHashMap<>();
    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4);
    private static final ExecutorService executorService =  new ThreadPoolExecutor(5, 200,
            60L, TimeUnit.SECONDS, new SynchronousQueue<>());

    private static class RateLimiterSingleton{
        private static RateLimiterHandler rateLimiterHandler = new RateLimiterHandler();
    }
    static {
        LOGGER.info("rateLimiterHandler begin work");
        init();
    }
    private static void init(){
        scheduledThreadPoolExecutor.scheduleAtFixedRate(()->{
            map.values().forEach(basicRateLimiter -> {
                basicRateLimiter.addToken(executorService);
            });
        },1000,10,TimeUnit.MILLISECONDS);
    }

    private RateLimiterHandler() {
    }

    public static RateLimiterHandler getInstance(){
        return RateLimiterSingleton.rateLimiterHandler;
    }

    public void add(String key, BasicRateLimiter rateLimiter){
        map.put(key,rateLimiter);
    }
    public BasicRateLimiter get(String key){
       return map.getOrDefault(key,null);
    }
    public void remove(String key){
        map.remove(key);
    }

}
