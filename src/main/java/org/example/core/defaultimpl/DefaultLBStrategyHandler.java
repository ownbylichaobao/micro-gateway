package org.example.core.defaultimpl;

import org.example.core.abs.AbstractLBStrategyHandler;
import org.example.entity.HostEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 默认实现 示例
 * @author lcb
 * @date 2020/5/7
 */
public class DefaultLBStrategyHandler extends AbstractLBStrategyHandler {
    /**
     * 默认随机法
     * @return 服务器地址
     */
    @Override
    public HostEntity getHost() {
        Random random = new Random();
        int pos = random.nextInt(hostEntities.size());
        return hostEntities.get(pos);
    }

    @Override
    public void loadData(String apiName) throws Exception{
//        HostEntity hostEntity = new HostEntity("localhost",8888,"test",0);
        HostEntity hostEntity1 = new HostEntity("localhost",8889,"",0);
        List<HostEntity> list = new ArrayList<>();
//        list.add(hostEntity);
        list.add(hostEntity1);
        this.hostEntities = list;
    }
}
