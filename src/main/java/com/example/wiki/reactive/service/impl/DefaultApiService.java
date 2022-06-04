package com.example.wiki.reactive.service.impl;

import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.service.ApiClient;
import com.example.wiki.reactive.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class DefaultApiService implements ApiService {
  private final ApiClient apiClient;

  @Override
  public Flux<RecentChange> getRecentChanges() {
    return apiClient.getRecentChanges()
        .bodyToFlux(RecentChange.class);
  }
}
