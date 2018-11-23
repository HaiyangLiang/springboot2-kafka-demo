package com.liang.kafka;

import com.liang.kafka.core.KafkaProducerMsgDispatcher;
import com.liang.kafka.producer.KafkaProducerListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author lianghaiyang 2018/11/22 10:03
 */
@SpringBootApplication
public class KafkaDemoApplication {

    @Resource
    private KafkaProducerListener kafkaProducerListener;

    public static void main(String[] args) {
        SpringApplication.run(KafkaDemoApplication.class, args);
    }

    @PostConstruct
    private void construct() {
        KafkaProducerMsgDispatcher dispatcher = new KafkaProducerMsgDispatcher(kafkaProducerListener.getBlockingDeque());
        dispatcher.setName("\r\n-------------KafkaProducerMsgDispatcher开始启动-------------------\r\n");
        dispatcher.start();
    }
}
