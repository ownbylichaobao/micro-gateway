package org.example.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.example.annotions.SimpleAutowired;
import org.example.annotions.SimpleComponent;
import org.example.core.ApiCacheHandler;
import org.example.core.RateLimiterHandler;
import org.example.core.basic.BasicFilter;
import org.example.core.basic.BasicParamHandler;
import org.example.core.basic.BasicRateLimiter;
import org.example.entity.HttpMainBody;
import org.example.entity.RequestMetadata;
import org.example.entity.ResponseData;
import org.example.utils.HttpUtils;
import org.example.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lcb
 * @date 2020/4/28
 */
@SimpleComponent
@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 1
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHandler.class);
    private AsciiString contentType = HttpHeaderValues.APPLICATION_JSON;
    @SimpleAutowired
    BasicParamHandler basicParamHandler;
    @SimpleAutowired
    BasicFilter basicFilter;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        RequestMetadata metadata = basicParamHandler.initRequestMetadata(msg);
        HttpMainBody httpMainBody;
        ResponseData responseData;
        try {
            basicFilter.doFilter(metadata);
            httpMainBody = ApiCacheHandler.getInstance().getHttpMainBody(metadata);
            BasicRateLimiter basicRateLimiter = RateLimiterHandler.getInstance().get(httpMainBody.getRateLimitKey());
            if(basicRateLimiter.acquire()){
                responseData = HttpUtils.exec(httpMainBody, metadata);
            }else{
                responseData = new ResponseData();
                responseData.setCode(1006);
                responseData.setMessage("rate limit");
            }
        } catch (Exception e) {
            LOGGER.error("请求出错 ->{}", e.toString());
            responseData = new ResponseData();
            responseData.setCode(500);
            responseData.setMessage(e.getMessage());
        }
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(JSONUtils.bean2Json(responseData).getBytes())); // 2

        HttpHeaders heads = response.headers();
        heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes()); // 3
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        ctx.flush(); // 4
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != cause) cause.printStackTrace();
        if (null != ctx) ctx.close();
    }
}
