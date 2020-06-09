package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author lcb
 * @date 2020/5/14
 */
@Data
@AllArgsConstructor
public class HostEntity {
    /**
     * ip
     */
    private String ip;
    /**
     * 端口
     */
    private int port;
    /**
     * 前缀
     */
    private String prefix;
    /**
     * 权重
     */
    private long weight;

    @Override
    public String toString() {
        return "http://"+ip+":"+port+ (prefix.startsWith("/")||"".equals(prefix)? prefix:"/"+prefix);
    }
}
