package org.example.core.defaultimpl;

import org.example.core.basic.BasicRateLimiter;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

/**
 * @author lcb
 * @date 2020/5/28
 */
public class DefaultRateLimiter implements BasicRateLimiter {
    private volatile int token;
    private final int allToken;
    private final int waitTime;
    private static long tokenOffsetAddr;
    private static Unsafe unsafe;
    private final Object lock = new Object();
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe =(Unsafe) field.get(null);
            tokenOffsetAddr = unsafe.objectFieldOffset(DefaultRateLimiter.class.getDeclaredField("token"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public DefaultRateLimiter( int waitTime) {
        this.allToken = this.token = 100;
        this.waitTime = waitTime;
    }

    @Override
    public boolean acquire() {
        int temp = token;
        if(temp<=0){
            temp = allToken;
        }
        long rest = waitTime;
        long futureTime = System.currentTimeMillis()+waitTime;
        while (temp>0){
            if(unsafe.compareAndSwapInt(this,tokenOffsetAddr,temp,temp-1)){
                return true;
            }
            temp = token;
            if(temp<=0&&rest>0){
                synchronized (lock){
                    try {
                        lock.wait(rest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                rest = futureTime - System.currentTimeMillis();
            }
        }
        return false;
    }

    @Override
    public void addToken(final ExecutorService executorService) {
        executorService.execute(()->{
            synchronized (lock){
                if(token<allToken){
                    this.token = this.token+1;
                }
                lock.notifyAll();
            }
        });
    }
}
