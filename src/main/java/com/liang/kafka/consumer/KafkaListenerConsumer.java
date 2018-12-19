package com.liang.kafka.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author lianghaiyang
 * @date 2018/12/19
 * 第二种方式配置消费者
 *
 */
@Slf4j
public class KafkaListenerConsumer implements MessageListener<String, String> {
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        //根据不同的主题进行消费
        String topic = data.topic();
        switch (topic) {
            case "topic_test":
                log.info("--------------topic_test---------------"+data.value());
                break;
            case "topic_test1":
                log.info("--------------topic_test---------------"+data.value());
                break;
            default:
                log.info("存在未监听的topic");
                break;
        }

    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment) {

    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Consumer<?, ?> consumer) {

    }

    @Override
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {

    }
}
