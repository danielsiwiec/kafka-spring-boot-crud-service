package com.dansiwiec;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.post;
import static io.restassured.RestAssured.put;
import static org.hamcrest.Matchers.equalTo;


import com.dansiwiec.kafka.Topics;
import com.dansiwiec.models.Todo;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {Topics.TODO},
    bootstrapServersProperty = "spring.kafka.bootstrap-servers", brokerProperties = {
    "log.dir=/tmp/out/embedded-kafka"
})
@RunWith(SpringRunner.class)
public class TodoTest {

  @LocalServerPort
  private int port;

  @Before
  public void setup() {
    RestAssured.port = port;
    RestAssured.requestSpecification = new RequestSpecBuilder()
        .setContentType(ContentType.JSON)
        .setAccept(ContentType.JSON)
        .build();
  }

  @Autowired
  Consumer<String, Todo> consumer;

  @Test
  public void shouldBeAbleToCreateTodo() {
    given()
        .body(Todo.builder().note("Get milk").build())
        .post("todo")
        .then()
        .statusCode(HttpStatus.SC_OK);
  }

  @Test
  public void shouldGetTodo() {
    Todo todo = given()
        .body(Todo.builder().note("Get milk").build())
        .post("todo")
        .as(Todo.class);

    get("/todo/{id}", Map.of("id", todo.getId()))
        .then()
        .body("note", equalTo("Get milk"));
  }

  @Test
  public void shouldUpdateTodo() {
    Todo todo = given()
        .body(Todo.builder().note("Get milk").build())
        .post("todo")
        .as(Todo.class);

    given()
        .body(Todo.builder().note("Get butter").build())
        .put("/todo/{id}", Map.of("id", todo.getId()))
        .then()
        .statusCode(200);

    get("/todo/{id}", Map.of("id", todo.getId()))
        .then()
        .body("note", equalTo("Get butter"));
  }
}

@Configuration
class EmbeddedKafkaConfiguration {
  @Bean
  public Consumer<String, Todo> testConsumer(EmbeddedKafkaBroker embeddedKafka) {
    var consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

    var cf = new DefaultKafkaConsumerFactory<String, Todo>(consumerProps);
    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, Topics.TODO);

    return consumer;
  }
}
