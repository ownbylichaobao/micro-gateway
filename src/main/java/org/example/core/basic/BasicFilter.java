package org.example.core.basic;

import org.example.entity.RequestMetadata;

/**
 * filter单元  在调用实际接口前会调用此filter,可以用来检查参数，鉴权等。
 * @author lcb
 * @date 2020/5/7
 */
public interface BasicFilter {
    /**
     * 执行filter
     * @param metadata request 请求元数据
     * @throws Exception 失败原因
     */
    void doFilter(RequestMetadata metadata) throws Exception;
    /**
     * filter实际动作，失败时使用抛出异常的方式  throws new Exception("错误原因")
     * @param metadata request 请求元数据
     * @throws Exception 失败原因
     */
    default void action(RequestMetadata metadata) throws Exception{
    }
}
