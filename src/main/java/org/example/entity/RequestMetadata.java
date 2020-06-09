package org.example.entity;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @author lcb
 * @date 2020/5/7
 */
@Data
@ToString
public class RequestMetadata {
    /**
     * 请求uri
     */
    private String uri;
    /**
     * ? 后的String
     */
    private String queryStr;
    /**
     * 请求方法
     */
    private HttpMethod method;
    /**
     * 请求头
     */
    private HttpHeaders headers;
    /**
     * 请求body
     */
    private String bodyJson;
    /**
     * url中的参数
     */
    private Map<String,Object> queryParamsMap;
    /**
     * form表单
     */
    private Map<String,Object> formParamsMap;
}
