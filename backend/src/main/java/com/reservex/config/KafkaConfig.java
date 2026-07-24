package com.reservex.config;

import com.reservex.kafka.event.BookingCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for:
 *  1. Topic creation
 *  2. Kafka Producer
 *  3. Kafka Consumer
 *  4. KafkaTemplate
 *  5. Kafka Listener
 */
@Configuration
public class KafkaConfig {

    // ── Kafka Constants ─────────────────────────────────────────────────────────

    /**
     * Topic used to publish booking created events.
     */
    public static final String BOOKING_CREATED_TOPIC =
            "booking-created";

    /**
     * Consumer group for booking events.
     */
    public static final String BOOKING_GROUP =
            "booking-group";

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // ── Bean 1: Kafka Topic ─────────────────────────────────────────────────────

    /**
     * Creates booking-created topic automatically
     * if it does not already exist.
     */
    @Bean
    public NewTopic bookingCreatedTopic() {

        return new NewTopic(
                BOOKING_CREATED_TOPIC,
                1,
                (short) 1
        );
    }

    // ── Bean 2: ProducerFactory ─────────────────────────────────────────────────

    /**
     * Producer configuration for publishing
     * BookingCreatedEvent messages.
     */
    @Bean
    public ProducerFactory<String, BookingCreatedEvent>
    producerFactory() {

        Map<String, Object> config =
                new HashMap<>();

        config.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );

        config.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        return new DefaultKafkaProducerFactory<>(
                config
        );
    }

    // ── Bean 3: KafkaTemplate ───────────────────────────────────────────────────

    /**
     * KafkaTemplate used by producers
     * to publish booking events.
     */
    @Bean
    public KafkaTemplate<String, BookingCreatedEvent>
    kafkaTemplate() {

        return new KafkaTemplate<>(
                producerFactory()
        );
    }

    // ── Bean 4: ConsumerFactory ─────────────────────────────────────────────────

    /**
     * Consumer configuration for reading
     * BookingCreatedEvent messages.
     */
    @Bean
    public ConsumerFactory<String, BookingCreatedEvent>
    consumerFactory() {

        JsonDeserializer<BookingCreatedEvent>
                deserializer =
                new JsonDeserializer<>(
                        BookingCreatedEvent.class
                );

        deserializer.addTrustedPackages(
                "com.reservex.kafka.event"
        );

        Map<String, Object> config =
                new HashMap<>();

        config.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers
        );

        config.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                BOOKING_GROUP
        );

        config.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );

        config.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class
        );

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                deserializer
        );
    }

    // ── Bean 5: Kafka Listener Container ────────────────────────────────────────

    /**
     * Enables @KafkaListener methods
     * for BookingCreatedEvent consumers.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<
            String,
            BookingCreatedEvent
            > kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<
                String,
                BookingCreatedEvent
                > factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
                consumerFactory()
        );

        return factory;
    }
}