package com.dansiwiec.kafka;

import com.dansiwiec.models.Todo;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfiguration {

  @Bean
  public KafkaStreams kafkaStreams(StreamsBuilder streamsBuilder, KafkaProperties kafkaProperties) {
    streamsBuilder.table(Topics.TODO, Consumed.with(Serdes.String(), new JsonSerde<>(Todo.class)), Materialized.as("todos"));
    Properties streamProperties = new Properties();
    streamProperties.putAll(kafkaProperties.buildStreamsProperties());
    KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), streamProperties);
    streams.cleanUp();
    streams.start();
    return streams;
  }
}
