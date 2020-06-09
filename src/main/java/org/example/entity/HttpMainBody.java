package org.example.entity;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import org.example.core.basic.BasicFilter;
import org.example.core.basic.BasicParamHandler;
import org.example.core.basic.BasicLBStrategyHandler;

/**
 * @author lcb
 * @date 2020/5/7
 */
@Data
public class HttpMainBody {
    /**
     * api
     */
    private String api;
    /**
     * 请求方法
     */
    private HttpMethod method;
    /**
     * 对应的策略获取器
     */
    private BasicLBStrategyHandler basicLBStrategyHandler;
    /**
     * 限流对应的key
     */
    private String rateLimitKey;

}
