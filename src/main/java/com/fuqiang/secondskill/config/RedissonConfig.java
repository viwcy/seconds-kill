package com.fuqiang.secondskill.config;

import com.alibaba.fastjson.JSON;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p> Title: RedissonConfig </p>
 * <p> Description: RedissonConfig </p>
 * <p> Copyright: Xi An BestTop Technologies, ltd. Copyright(c) 2020 </p>
 * TODO
 *
 * @Author Fuqiang
 * @Version 0.0.1
 * Created by Fu on 2020/7/2 0002 9:46
 */
@Configuration
public class RedissonConfig {

    private static final String REDIS_URL_PREFIX = "redis://";
    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Bean
    public RedissonClient getRedisson() {
        Config config = new Config();
        /** 单节点配置 */
//        config.useSingleServer().setAddress(RedissonConfig.REDIS_URL_PREFIX + host + ":" + port).setConnectTimeout(3000).setTimeout(3000);
        String[] split = clusterNodes.split(",");
        String[] strings = new String[split.length];
        for (int i = 0; i < split.length; i++) {
            strings[i] = REDIS_URL_PREFIX + split[i];
        }
        System.out.println(JSON.toJSONString(strings));
        /** 集群状态扫描间隔，单位ms；nodes节点 */
        config.useClusterServers().setScanInterval(10000).addNodeAddress(strings);
        return Redisson.create(config);
    }
}
