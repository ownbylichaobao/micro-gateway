package org.example.core.basic;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.CharsetUtil;
import org.example.entity.RequestMetadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数解析器，默认已经实现参数的获取方法
 * @author lcb
 * @date 2020/5/7
 */
public interface BasicParamHandler {
    /**
     * 组装请求元数据
     * @param request request
     * @return RequestMetadata
     */
    default RequestMetadata initRequestMetadata(FullHttpRequest request){
        RequestMetadata requestMetadata = new RequestMetadata();
        requestMetadata.setUri(request.uri().split("\\?")[0]);
        requestMetadata.setQueryStr((request.uri()+"?").split("\\?").length >1 ?(request.uri()+"?").split("\\?")[1]:(request.uri()+"?").split("\\?")[0] );
        requestMetadata.setMethod(request.method());
        requestMetadata.setBodyJson(parseBody(request));
        requestMetadata.setHeaders(parseHeader(request));
        requestMetadata.setFormParamsMap(parseForm(request));
        requestMetadata.setQueryParamsMap(parseQuery(request));
        return requestMetadata;
    }
    /**
     * 解析header
     * @param request request
     * @return HttpHeaders
     */
    default HttpHeaders parseHeader(FullHttpRequest request){
        return request.headers();
    }

    /**
     * 解析body
     * @param request request
     * @return String
     */
    default String parseBody(FullHttpRequest request){
        return request.content().isReadable() ?
                request.content().toString(CharsetUtil.UTF_8)
                : "";
    }

    /**
     * 解析query 参数
     * @param request request
     * @return map
     */
    default Map<String,Object> parseQuery(FullHttpRequest request){
        Map<String,Object> paramMap = new HashMap<>();
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        decoder.parameters().forEach(paramMap::put);
        return paramMap;
    }

    /**
     * 解析Form表单
     * @param request request
     * @return map
     */
    default Map<String,Object> parseForm(FullHttpRequest request){
        Map<String,Object> paramMap = new HashMap<>();
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        decoder.getBodyHttpDatas().forEach(param ->{
            Attribute data = (Attribute)param;
            try {
                paramMap.put(data.getName(),data.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return paramMap;
    }
}
