package org.example.utils;

import io.netty.handler.codec.http.HttpMethod;
import kong.unirest.*;
import org.apache.http.HttpHeaders;
import org.example.entity.HttpMainBody;
import org.example.entity.RequestMetadata;
import org.example.entity.ResponseData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * @author lcb
 * @date 2020/5/14
 */
public class HttpUtils {
    public static ResponseData exec(HttpMainBody httpMainBody, RequestMetadata requestMetadata) throws Exception {
        if (HttpMethod
                .GET.equals(httpMainBody.getMethod())) {
            return doGet(httpMainBody, requestMetadata);
        }
        if (HttpMethod
                .POST.equals(httpMainBody.getMethod())) {
            return doPost(httpMainBody, requestMetadata);
        }
        throw new UnsupportedOperationException("不支持的请求方法");
    }

    private static ResponseData doGet(HttpMainBody httpMainBody, RequestMetadata requestMetadata) throws Exception {
        GetRequest getRequest = Unirest.get(urlPack(httpMainBody, requestMetadata));
        for (Map.Entry<String, String> entry : requestMetadata.getHeaders().entries()) {
            getRequest = getRequest.header(entry.getKey(), entry.getValue());
        }
        HttpResponse<JsonNode> httpResponse = getRequest.asJson();
        return new ResponseData(200, "message", httpResponse.getBody().getObject());
    }

    private static ResponseData doPost(HttpMainBody httpMainBody, RequestMetadata requestMetadata) throws Exception {
        HttpRequestWithBody postRequest = Unirest.post(urlPack(httpMainBody, requestMetadata));
        HttpResponse<JsonNode> httpResponse;
        for (Map.Entry<String, String> entry : requestMetadata.getHeaders().entries()) {
            if (!entry.getKey().equals(HttpHeaders.CONTENT_LENGTH)) {
                postRequest = postRequest.header(entry.getKey(), entry.getValue());
            }
        }
        switch (requestMetadata.getHeaders().get(HttpHeaders.CONTENT_TYPE)) {
            case "application/json":
                RequestBodyEntity requestBodyEntity = postRequest.body(requestMetadata.getBodyJson());
                httpResponse = requestBodyEntity.asJson();
                break;
            case "application/x-www-form-urlencoded":
                MultipartBody multipartBody = postRequest.fields(requestMetadata.getFormParamsMap());
                httpResponse = multipartBody.asJson();
                break;
            default:
                throw new Exception("不支持的Content-Type");

        }
        return new ResponseData(200, "success", httpResponse.getBody().getObject());

    }

    private static String urlPack(HttpMainBody httpMainBody, RequestMetadata requestMetadata) {
        String url = httpMainBody.getBasicLBStrategyHandler().getHost().toString();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        String api = httpMainBody.getApi();
        if (api.startsWith("/")) {
            api = api.substring(1);
        }
        if (api.endsWith("/")) {
            api = api.substring(0, api.length() - 1);
        }
        String queryStr = requestMetadata.getQueryStr();
        if (queryStr.startsWith("?")) {
            queryStr = queryStr.substring(1);
        }
        return url + "/" + api + "?" + queryStr;
    }
}
