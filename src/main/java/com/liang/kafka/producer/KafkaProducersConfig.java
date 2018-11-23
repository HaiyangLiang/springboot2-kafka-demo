package com.liang.kafka.producer;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lianghaiyang 2018/11/6 10:58
 */
@Configuration
public class KafkaProducersConfig {
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;
    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;
    @Value("${spring.kafka.producer.batch-size}")
    private int batchSize;
    @Value("${spring.kafka.producer.buffer-memory}")
    private int bufferMemory;
    @Value("${spring.kafka.producer.retries}")
    private int retries;
    @Value("${spring.kafka.producer.linger}")
    private int linger;
    @Value("${spring.kafka.producer.topic}")
    private String topic;
    @Value("${spring.kafka.producer.properties.sasl.mechanism}")
    private String saslMechanism;
    @Value("${spring.kafka.producer.properties.security.protocol}")
    private String securityProtocol;
    @Value("${spring.kafka.producer.properties.sasl.jaas.config}")
    private String saslJaasConfig;
    @Resource
    private KafkaProducerListener kafkaProducerListener;

    @Bean("kafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<String, String>(producerFactory());
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        kafkaTemplate.setDefaultTopic(topic);
        return kafkaTemplate;
    }

    private ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = new HashMap<>(9);
        if (StringUtils.isNotEmpty(saslJaasConfig)) {
            // 设置sasl认证的两种方式
//            System.setProperty("java.security.auth.login.config", "classpath:/application.properties:/kafka_client_jaas.conf");
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        properties.put(ProducerConfig.RETRIES_CONFIG, retries);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        if (StringUtils.isNotEmpty(saslMechanism) && StringUtils.isNotEmpty(securityProtocol)) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        }
        return new DefaultKafkaProducerFactory<>(properties);
    }
}