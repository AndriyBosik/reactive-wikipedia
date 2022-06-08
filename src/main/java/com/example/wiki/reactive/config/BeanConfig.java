package com.example.wiki.reactive.config;

import com.example.wiki.reactive.model.RecentChange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

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

  @Bean
  public Flux<RecentChange> recentChangeFlux(WebClient webClient) {
    return webClient.get().uri("/recentchange")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(RecentChange.class);
  }
}
