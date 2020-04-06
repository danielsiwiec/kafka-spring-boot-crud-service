package com.dansiwiec.controllers;

import com.dansiwiec.dao.TodoDao;
import com.dansiwiec.models.Todo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
  private TodoDao todoDao;

  @PostMapping
  public Todo create(@RequestBody Todo todo) {
    return todoDao.create(todo);
  }

  @GetMapping("/{id}")
  public Todo get(@PathVariable("id") String id) {
    return todoDao.get(id);
  }

  @PutMapping("/{id}")
  public Todo update(@PathVariable("id") String id, @RequestBody Todo todo) {
    return todoDao.update(id, todo);
  }
}
