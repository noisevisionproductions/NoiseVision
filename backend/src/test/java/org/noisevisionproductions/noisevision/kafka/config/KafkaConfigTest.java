package org.noisevisionproductions.noisevision.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.noisevisionproductions.noisevision.kafka.event.dto.UserRegistrationEvent;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class KafkaConfigTest {

    @InjectMocks
    private KafkaConfig kafkaConfig;

    private final String testBootstrapServers = "localhost:9092";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaConfig, "bootstrapServers", testBootstrapServers);
    }

    @Test
    void shouldCreateRegistrationEventsTopic() {
        NewTopic topic = kafkaConfig.registrationEventsTopic();

        assertThat(topic).isNotNull();
        assertThat(topic.name()).isEqualTo("user-registration-events");
        assertThat(topic.numPartitions()).isEqualTo(1);
        assertThat(topic.replicationFactor()).isEqualTo((short) 1);
    }

    @Test
    void shouldCreateProducerFactoryWithCorrectConfiguration() {
        ProducerFactory<String, Object> producerFactory = kafkaConfig.producerFactory();

        assertThat(producerFactory).isNotNull();
        assertThat(producerFactory.getConfigurationProperties())
                .containsEntry(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, testBootstrapServers)
                .containsEntry(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class)
                .containsEntry(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    }

    @Test
    void shouldCreateKafkaTemplateWithCorrectConfiguration() {
        KafkaTemplate<String, Object> kafkaTemplate = kafkaConfig.kafkaTemplate();

        assertThat(kafkaTemplate).isNotNull();
        assertThat(kafkaTemplate.getProducerFactory()).isNotNull();
    }

    @Test
    void shouldCreateConsumerFactoryWithCorrectConfiguration() {
        ConsumerFactory<String, UserRegistrationEvent> consumerFactory = kafkaConfig.consumerFactory();

        assertThat(consumerFactory).isNotNull();
        assertThat(consumerFactory.getConfigurationProperties())
                .containsEntry(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, testBootstrapServers)
                .containsEntry(ConsumerConfig.GROUP_ID_CONFIG, "portfolio-group")
                .containsEntry(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class)
                .containsEntry(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class)
                .containsEntry(JsonDeserializer.TRUSTED_PACKAGES, "*");
    }

    @Test
    void shouldCreateKafkaListenerContainerFactoryWithCorrectConfiguration() {
        var listenerFactory = kafkaConfig.kafkaListenerContainerFactory();

        assertThat(listenerFactory).isNotNull();
        assertThat(listenerFactory.getConsumerFactory()).isNotNull();
    }
}