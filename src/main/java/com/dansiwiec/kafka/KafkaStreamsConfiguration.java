package com.dansiwiec.kafka;

import com.dansiwiec.models.Todo;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.support.serializer.JsonSerde;

@Configuration
@EnableKafkaStreams
@EnableKafka
public class KafkaStreamsConfiguration {

  @Bean
  public KTable<String, Todo> todoTable(StreamsBuilder streamsBuilder) {
    return streamsBuilder.table(
        Topics.TODO,
        Consumed.with(Serdes.String(), new JsonSerde<>(Todo.class)),
        Materialized.as(Stores.TODOS)
    );
  }
}
