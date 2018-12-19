package com.liang.kafka.consumer;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lianghaiyang 2018/11/22 10:04
 */
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String servers;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String autoOffsetReset;
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    @Value("${spring.kafka.consumer.properties.sasl.mechanism}")
    private String saslMechanism;
    @Value("${spring.kafka.consumer.properties.security.protocol}")
    private String securityProtocol;
    @Value("${spring.kafka.consumer.properties.sasl.jaas.config}")
    private String saslJaasConfig;
    @Value("${spring.kafka.consumer.topics}")
    private String topics;
    /**
     * 工厂配置
     *
     * 关于consumer的主要的封装在ConcurrentKafkaListenerContainerFactory这个里头，
     * 本身的KafkaConsumer是线程不安全的，无法并发操作，这里spring又在包装了一层，
     * 根据配置的spring.kafka.listener.concurrency来生成多个并发的KafkaMessageListenerContainer实例
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(1);
        factory.getContainerProperties().setPollTimeout(1500);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    /**
     *  消费者监听器配置
     *
     *  每个KafkaMessageListenerContainer都自己创建一个ListenerConsumer，
     *  然后自己创建一个独立的kafka consumer，每个ListenerConsumer在线程池里头运行，这样来实现并发。
     *
     *  每个ListenerConsumer里头都有一个recordsToProcess队列，从原始的kafka consumer poll出来的记录会放到这个队列里头，
     *  然后有一个ListenerInvoker线程循环超时等待从recordsToProcess取出记录，然后交给应用程序的KafkaListener标注的方法去执行
     */
    @Bean
    public KafkaMessageListenerContainer<String, String> listenerContainer(ConsumerFactory<String, String> cf) {
        // 设置topics
        ContainerProperties containerProperties = new ContainerProperties(topics);
        // 设置消费者监听器
        containerProperties.setMessageListener(new KafkaListenerConsumer());
        KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(cf, containerProperties);
        container.setBeanName("messageListenerContainer");
        return container;
    }

    /**
     * 消费者基本配置
     */
    private Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        // 如果有sasl认证进行配置, 没有则不配置
        if (!StringUtils.isEmpty(securityProtocol) && !StringUtils.isEmpty(saslMechanism) && !StringUtils.isEmpty(saslJaasConfig)) {
            propsMap.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            propsMap.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
            propsMap.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        return propsMap;
    }

}