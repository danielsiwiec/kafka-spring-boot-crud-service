package com.dansiwiec.dao;

import com.dansiwiec.kafka.Stores;
import com.dansiwiec.kafka.Topics;
import com.dansiwiec.models.Todo;
import java.util.UUID;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TodoDao {

  @Autowired
  StreamsBuilderFactoryBean streamsBuilderFactory;

  @Autowired
  private KafkaTemplate<String, Todo> kafkaTemplate;

  public Todo get(String id) {
    return streamsBuilderFactory.getKafkaStreams()
        .<ReadOnlyKeyValueStore<String, Todo>>store(Stores.TODOS, QueryableStoreTypes.keyValueStore())
        .get(id);
  }

  public Todo create(Todo todoRequest) {
    Todo todo = todoRequest.toBuilder().id(UUID.randomUUID().toString()).build();
    kafkaTemplate.send(Topics.TODO, todo.getId(), todo);
    return todo;
  }

  public Todo update(String id, Todo todoUpdate) {
    Todo todo = todoUpdate.toBuilder().id(id).build();
    kafkaTemplate.send(Topics.TODO, id, todo);
    return get(id);
  }
}
