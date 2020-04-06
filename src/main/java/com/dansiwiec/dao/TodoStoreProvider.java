package com.dansiwiec.dao;

import com.dansiwiec.kafka.Stores;
import com.dansiwiec.models.Todo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Service;

@Service
public class TodoStoreProvider {

  @Autowired
  StreamsBuilderFactoryBean streamsBuilderFactory;

  public ReadOnlyKeyValueStore<String, Todo> store() {
    return streamsBuilderFactory.getKafkaStreams().store(Stores.TODOS, QueryableStoreTypes.keyValueStore());
  }

}
