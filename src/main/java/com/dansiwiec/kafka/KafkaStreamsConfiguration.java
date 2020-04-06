package com.dansiwiec.kafka;

import com.dansiwiec.models.Todo;
import java.util.Map;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
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
  public StreamsConfig streamsConfig(KafkaProperties kafkaProperties) {
    Map<String, Object> streamProps = kafkaProperties.buildStreamsProperties();
    return new StreamsConfig(streamProps);
  }

  @Bean
  public KafkaStreams kafkaStreams(StreamsBuilder streamsBuilder, StreamsConfig streamsConfig) {
    streamsBuilder.table(Topics.TODO, Consumed.with(Serdes.String(), new JsonSerde<>(Todo.class)), Materialized.as("todos"));
    KafkaStreams streams = new KafkaStreams(streamsBuilder.build(), streamsConfig);
    streams.cleanUp();
    streams.start();
    return streams;
  }
}
