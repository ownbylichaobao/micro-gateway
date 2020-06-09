package org.example.core.defaultimpl;

import org.example.core.abs.AbstractFilter;
import org.example.entity.RequestMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认实现  示例
 * @author lcb
 * @date 2020/5/7
 */
public class DefaultFilterImpl extends AbstractFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFilterImpl.class);
    @Override
    public void action(RequestMetadata metadata) throws Exception{
        LOGGER.info("进入defaultFilter中 apiName->{}",metadata.getUri());
    }
}
