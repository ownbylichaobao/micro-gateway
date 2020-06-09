package org.example.config;

import org.example.annotions.SimpleBean;
import org.example.annotions.SimpleConfiguration;
import org.example.core.defaultimpl.DefaultBasicParamHandlerImpl;
import org.example.core.defaultimpl.DefaultFilterImpl;
import org.example.core.basic.BasicFilter;
import org.example.core.basic.BasicParamHandler;

/**
 * 配置类 示例
 * @author lcb
 * @date 2020/5/13
 */
@SimpleConfiguration
public class DefaultConfiguration {
    @SimpleBean
    public BasicFilter basicFilter(){
        return new DefaultFilterImpl();
    }
    @SimpleBean
    public BasicParamHandler basicParamHandler(){
        return new DefaultBasicParamHandlerImpl();
    }
}
