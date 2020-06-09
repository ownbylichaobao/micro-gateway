package org.example.core.basic;

import org.example.entity.HostEntity;

import java.util.List;
import java.util.Set;

/**
 * 负载均衡策略 使用loadData加载数据，getHost实现具体负载均衡策略
 * @author lcb
 * @date 2020/5/7
 */
public interface BasicLBStrategyHandler {
    HostEntity getHost();

    /**
     * 加载数据，可以从数据库中或者任何其他地方
     * @param apiName 接口名
     * @throws Exception
     */
    void loadData(String apiName) throws Exception;
}
