package com.example.wiki.reactive.service.impl;

import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.repository.RecentChangeRepository;
import com.example.wiki.reactive.service.RecentChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DefaultRecentChangeService implements RecentChangeService {
  private final RecentChangeRepository recentChangeRepository;

  @Override
  public Flux<RecentChange> getRecentChanges() {
    return recentChangeRepository.findAllBy();
  }

  @Override
  public Flux<RecentChange> getUsersRecentChanges(Set<String> users) {
    return recentChangeRepository.findAllBy()
        .filter(recentChange -> ifContainsUser(users, recentChange));
  }

  private boolean ifContainsUser(Set<String> users, RecentChange recentChange) {
    return users.stream()
        .map(String::toLowerCase)
        .anyMatch(user -> user.equalsIgnoreCase(recentChange.getUser()));
  }
}
