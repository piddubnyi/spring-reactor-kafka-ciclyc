package com.example.demo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public Map<String, Object> kafkaProps() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092",
            ConsumerConfig.GROUP_ID_CONFIG, DemoApplication.class.getSimpleName()
        );
    }

    @Bean
    public ReceiverOptions<String, String> receiverOptions(Map<String, Object> kafkaProps) {
        return ReceiverOptions.<String, String>create(kafkaProps)
			.withKeyDeserializer(new StringDeserializer())
			.withValueDeserializer(new StringDeserializer())
            .subscription(List.of("test-topic"));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, String> template(ReceiverOptions<String, String> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

    @Bean
    public ApplicationRunner runner(ReactiveKafkaConsumerTemplate<String, String> kafkaReceiver) {
        return args -> kafkaReceiver.receive()
			.log()
			.subscribe();
    }
}
