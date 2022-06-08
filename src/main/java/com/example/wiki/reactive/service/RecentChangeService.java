package com.example.wiki.reactive.service;

import com.example.wiki.reactive.model.RecentChange;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface RecentChangeService {
  Flux<RecentChange> getRecentChanges();

  Flux<RecentChange> getUsersRecentChanges(Set<String> users);
}
