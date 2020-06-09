package org.example.core;

import org.example.core.basic.BasicLBStrategyHandler;
import org.example.core.basic.BasicRateLimiter;
import org.example.core.defaultimpl.DefaultLBStrategyHandler;
import org.example.core.defaultimpl.DefaultRateLimiter;
import org.example.entity.HttpMainBody;
import org.example.entity.RequestMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HttpMainBody 缓存池  使用LRU淘汰策略
 * @author lcb
 * @date 2020/5/13
 */
public class ApiCacheHandler {
    private ApiCacheHandler(){

    }
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiCacheHandler.class);
    private static final Map<String, HttpMainBody> cacheMap = new MyLRUCache<>();
    private static class ApiCacheHandlerSingle{
        private static final ApiCacheHandler apiCacheHandler = new ApiCacheHandler();
    }
    private static class MyLRUCache<K,V> extends LinkedHashMap<K,V>{
        private final int maxCapacity;
        public MyLRUCache(){
            super(10,0.75f,true);
            this.maxCapacity = 50;
        }
        public MyLRUCache(int initCapacity,int maxCapacity){
            super(initCapacity,0.75f,true);
            this.maxCapacity = maxCapacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            boolean ifRemove = size() > maxCapacity;
            if(ifRemove){
               LOGGER.info("LRU 策略执行 -> {}",eldest.getKey());
               HttpMainBody value = (HttpMainBody) eldest.getValue();
               RateLimiterHandler.getInstance().remove(value.getRateLimitKey());
            }
            return ifRemove;
        }
    }
    public static ApiCacheHandler getInstance(){
        return ApiCacheHandlerSingle.apiCacheHandler;
    }
    public synchronized void addHttpMainBody(HttpMainBody httpMainBody){
        cacheMap.put(httpMainBody.getApi(),httpMainBody);
    }
    public HttpMainBody getHttpMainBody( RequestMetadata metadata) throws Exception{
        if(cacheMap.containsKey(metadata.getUri())){
            return cacheMap.get(metadata.getUri());
        }
        LOGGER.info("未找到api ->{} 对应的请求元数据 ，进行初始构造。。。",metadata.getUri());
        HttpMainBody httpMainBody = new HttpMainBody();
        httpMainBody.setApi(metadata.getUri());
        httpMainBody.setMethod(metadata.getMethod());
        BasicLBStrategyHandler lbStrategyHandler = new DefaultLBStrategyHandler();
        lbStrategyHandler.loadData(metadata.getUri());
        httpMainBody.setBasicLBStrategyHandler(lbStrategyHandler);
        BasicRateLimiter basicRateLimiter = new DefaultRateLimiter(1000*2);
        httpMainBody.setRateLimitKey(metadata.getUri());
        RateLimiterHandler.getInstance().add(httpMainBody.getRateLimitKey(),basicRateLimiter);
        addHttpMainBody(httpMainBody);
        return httpMainBody;
    }
}
