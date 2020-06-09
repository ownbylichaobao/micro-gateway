package org.example.core.abs;

import org.example.core.basic.BasicFilter;
import org.example.entity.RequestMetadata;

/**
 * 实现doFilter方法，构成filter调用的初态
 * @author lcb
 * @date 2020/5/7
 */
public abstract class AbstractFilter implements BasicFilter {
    private BasicFilter nextFilter;
    @Override
    final public void doFilter(RequestMetadata metadata)throws Exception{
        action(metadata);
        if(nextFilter!=null){
            nextFilter.doFilter(metadata);
        }
    }
    final public void setNextFilter(BasicFilter nextFilter) {
        this.nextFilter = nextFilter;
    }
}
