package com.liang.kafka.config;

import com.liang.kafka.producer.KafkaProducerListener;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author lianghaiyang 2018/10/30 11:27
 */
@Component
@ConfigurationProperties(prefix = "listener")
@Getter
@Setter
@ToString
@Slf4j
public class ListenerConfig {
    /**
     * 配置线程池大小
     */
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer poolQueueCapacity;
    private String threadNamePrefix;
    private Boolean visiblePool;
    /**
     * 配置kafka回调监听器
     */
    private Boolean visibleLog;
    private Integer listenerQueueSize;

    @Bean
    public KafkaProducerListener kafkaProducerListener() {
        BlockingQueue<String> blockingDeque = new ArrayBlockingQueue<>(listenerQueueSize);
        return new KafkaProducerListener(blockingDeque, this.visibleLog);
    }
}
