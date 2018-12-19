package com.liang.kafka.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;

import java.util.concurrent.BlockingQueue;

/**
 * @author lianghaiyang
 * @date 2018/11/07
 */
public class KafkaProducerListener implements ProducerListener<String, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerListener.class);
    private final BlockingQueue<String> blockingDeque;
    private final Boolean visibleLog;

    public KafkaProducerListener(BlockingQueue<String> blockingDeque, Boolean visibleLog) {
        this.blockingDeque = blockingDeque;
        this.visibleLog = visibleLog;
    }

    /**
     * 获取消费成功队列,统一进行处理
     */
    public BlockingQueue<String> getBlockingDeque() {
        return blockingDeque;
    }

    @Override
    public void onSuccess(String topic, Integer partition, String key, String value, RecordMetadata recordMetadata) {
        blockingDeque.add(value);
        if (visibleLog) {
//            LOGGER.info("kafka生产者key:" + key + " kafka消费者value:" + value);
        }
    }

    @Override
    public void onError(String topic, Integer partition, String key, String value, Exception exception) {
        LOGGER.error("message:" + value + "failed");
        exception.printStackTrace();
    }

}
