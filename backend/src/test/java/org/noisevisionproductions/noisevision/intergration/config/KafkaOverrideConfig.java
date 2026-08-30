package org.noisevisionproductions.noisevision.intergration.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.ConnectException;
import java.util.Map;

@TestConfiguration
public class KafkaOverrideConfig {

    @Bean
    @Primary
    public ProducerFactory<String, Object> producerFactory(EmbeddedKafkaBroker embeddedKafkaBroker, ObjectMapper objectMapper) {
        Map<String, Object> props = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(props);
        factory.setValueSerializer(new JsonSerializer<>(objectMapper));
        return factory;
    }

    @Bean
    @Primary
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @Primary
    public ConsumerFactory<String, Object> consumerFactory(EmbeddedKafkaBroker embeddedKafkaBroker, ObjectMapper objectMapper) {
        Map<String, Object> props = KafkaTestUtils.consumerProps("portfolio-test-group", "false", embeddedKafkaBroker);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<Object> jsonDeserializer =
                new JsonDeserializer<>(objectMapper);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), jsonDeserializer);
    }

    @Bean(name = "kafkaListenerContainerFactory")
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + "-failed", record.partition()));

        DefaultErrorHandler errorHandler = getErrorHandler(recoverer);
        errorHandler.addRetryableExceptions(
                HttpServerErrorException.class,
                ResourceAccessException.class,
                ConnectException.class
        );

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    private @NonNull DefaultErrorHandler getErrorHandler(DeadLetterPublishingRecoverer recoverer) {

        BackOff customBackOff = new BackOff() {

            @Override
            @NonNull
            public BackOffExecution start() {

                return new BackOffExecution() {
                    private int attempt = 0;
                    private final long[] intervals = {3000L, 7000L, 15000L};

                    @Override
                    public long nextBackOff() {
                        if (attempt >= intervals.length) return BackOffExecution.STOP;
                        return intervals[attempt++];
                    }
                };
            }
        };

        return new DefaultErrorHandler(recoverer, customBackOff);
    }
}
