package com.dansiwiec.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Todo {

  String id;
  String note;
}
