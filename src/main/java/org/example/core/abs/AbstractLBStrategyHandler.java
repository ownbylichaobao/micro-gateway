package org.example.core.abs;

import org.example.core.basic.BasicLBStrategyHandler;
import org.example.entity.HostEntity;

import java.util.List;
import java.util.Set;

/**
 * 提供hostEntities 初步实现加载数据功能
 * @author lcb
 * @date 2020/5/7
 */
public abstract class AbstractLBStrategyHandler implements BasicLBStrategyHandler {
    public List<HostEntity> hostEntities;
    public void loadData(List<HostEntity> hostEntities) {
        this.hostEntities = hostEntities;
    }
}
