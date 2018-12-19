# springboot2-kafka-demo
springboot2.x集成kafka生产者消费者
 ## 生产者配置: 
     (1). 集成sasl安全认证
     (2). 发送成功或者失败进行回调
     (3). excel工具封装
     (4). 自定义线程池使用
     
## 生产者配置的两种方式: 
  ###  1. 通过注解方式
        1> 需要的消费者配置
            /**
             * 工厂配置
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
             * 消费消息统一处理
             */
            @KafkaListener(topics = "${spring.kafka.consumer.topic}")
            public void listen(ConsumerRecord<?, ?> record) {
                log.info("消费kafka的key: " + record.key());
                log.info("消费kafka的value: " + record.value().toString());
            }
            
   ###  2. 通过配置监听器方式
          /**
             * 工厂配置
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
             *  消费者监听器配置，添加具体业务逻辑代码，可以继承多种监听器
             */
            public class KafkaListenerConsumer implements MessageListener<String, String> {...}