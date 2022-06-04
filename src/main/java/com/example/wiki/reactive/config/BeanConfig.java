package com.example.wiki.reactive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfig {
  private final String apiBaseUrl;

  public BeanConfig(@Value("${wiki.api.base-url}") String apiBaseUrl) {
    this.apiBaseUrl = apiBaseUrl;
  }

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .baseUrl(apiBaseUrl)
        .build();
  }
}
