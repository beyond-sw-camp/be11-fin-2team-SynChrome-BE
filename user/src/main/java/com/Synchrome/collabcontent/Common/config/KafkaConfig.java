package com.Synchrome.collabcontent.Common.config;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.*;
//import org.springframework.kafka.support.serializer.JsonSerializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
public class KafkaConfig {
//    @Value("${spring.kafka.kafka-server}")
//    private String kafkaServer;
//
//    @Value("${spring.kafka.consumer.group-id}")
//    private String groupId;
//    @Value("${spring.kafka.consumer.auto-offset-reset}")
//    private String offset;
//
//    @Bean
//    public ProducerFactory<String,Object> producerFactory(){
//        Map<String, Object> config = new HashMap<>();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaServer);
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        return new DefaultKafkaProducerFactory<>(config);
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate(){
//        return new KafkaTemplate<>(producerFactory());
//    }
//
//    @Bean
//    public ConsumerFactory<String,Object> consumerFactory(){
//        Map<String, Object> config = new HashMap<>();
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaServer);
//        config.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);
//        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,offset);
//        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(config);
//    }
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String,String> kafkaListener(){
//        ConcurrentKafkaListenerContainerFactory<String,String> listener = new ConcurrentKafkaListenerContainerFactory<>();
//        listener.setConsumerFactory(consumerFactory());
//        return listener;
//    }
}
