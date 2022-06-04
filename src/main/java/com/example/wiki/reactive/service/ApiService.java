package com.example.wiki.reactive.service;

import com.example.wiki.reactive.model.RecentChange;
import reactor.core.publisher.Flux;

public interface ApiService {
  Flux<RecentChange> getRecentChanges();
}
