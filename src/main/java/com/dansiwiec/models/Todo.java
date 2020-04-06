package com.dansiwiec.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Todo {

  String id;
  String note;
}
