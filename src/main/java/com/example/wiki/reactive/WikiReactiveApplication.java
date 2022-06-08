package com.example.wiki.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class WikiReactiveApplication {

  public static void main(String[] args) {
    SpringApplication.run(WikiReactiveApplication.class, args);
  }
}
