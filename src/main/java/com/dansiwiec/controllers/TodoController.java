package com.dansiwiec.controllers;

import com.dansiwiec.kafka.Topics;
import com.dansiwiec.models.Todo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "/todo")
@RequiredArgsConstructor
public class TodoController {

  @Autowired
  private KafkaTemplate<String, Todo> kafkaTemplate;

  @Autowired
  private KafkaStreams streams;

  @PostMapping
  public Todo create(@RequestBody Todo todoRequest) {
    Todo todo = Todo.builder()
        .id(UUID.randomUUID().toString())
        .note(todoRequest.getNote()).build();
    this.kafkaTemplate.send(Topics.TODO, todo.getId(), todo);
    return todo;
  }

  @GetMapping("/{id}")
  public Todo get(@PathVariable("id") String id) {
    ReadOnlyKeyValueStore<String, Todo> todos = streams.store("todos", QueryableStoreTypes.keyValueStore());
    return todos.get(id);
  }


  @PutMapping("/{id}")
  public Todo update(@PathVariable("id") String id, @RequestBody Todo newTodo) {
    ReadOnlyKeyValueStore<String, Todo> todos = streams.store("todos", QueryableStoreTypes.keyValueStore());
    Todo todo = Todo.builder()
        .id(id)
        .note(newTodo.getNote()).build();
    this.kafkaTemplate.send(Topics.TODO, id, todo);
    return todos.get(id);
  }
}
