package org.example.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.example.annotions.SimpleAutowired;
import org.example.annotions.SimpleComponent;
import org.example.config.BaseConfigService;
import org.example.core.AutowireFactory;
import org.example.core.BeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author lcb
 * @date 2020/4/28
 */
@SimpleComponent(name = "StartService")
public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    @SimpleAutowired
    HttpHandler httpHandler;
    public static void main(String[] args) throws Exception {

        BeanFactory.getInstance().init(BaseConfigService.basePackage);
        Server server = (Server) BeanFactory.getInstance().getBean("StartService");
        server.start(BaseConfigService.port);
    }

    public void start(int port) throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch)
                            throws Exception {
                        LOGGER.info("initChannel ch:{}", ch);
                        ch.pipeline()
                                .addLast("decoder", new HttpRequestDecoder())   // 1
                                .addLast("encoder", new HttpResponseEncoder())  // 2
                                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))    // 3
                                .addLast("handler", httpHandler);        // 4
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        b.bind(port).sync();
        LOGGER.info("服务启动成功：端口->{}", port);
    }
}
