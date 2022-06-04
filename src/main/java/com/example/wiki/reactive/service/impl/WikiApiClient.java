package com.example.wiki.reactive.service.impl;

import com.example.wiki.reactive.service.ApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class WikiApiClient implements ApiClient {
  private final WebClient webClient;

  @Override
  public WebClient.ResponseSpec getRecentChanges() {
    return webClient
        .get()
        .uri("/recentchange")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve();
  }
}
