package org.example.config;

import org.example.annotions.SimpleComponent;
import org.example.core.BeanFactory;
import org.example.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author lcb
 * @date 2020/5/14
 */

@SimpleComponent
public class BaseConfigService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseConfigService.class);
    public static int port;
    public static String basePackage;
    static {
        try {
            loadConfig();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("配置加载错误 ->{}",e.getMessage());
        }
    }
    private static void loadConfig() throws Exception{
        LOGGER.info("读取配置文件配置 ->application.properties");
        InputStream inStream = Server.class.getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        properties.load(inStream);
        basePackage = properties.getProperty("base.package",Server.class.getPackage().getName());
        port = Integer.parseInt(properties.getProperty("micro-gateway.port","8080"));
    }
}
