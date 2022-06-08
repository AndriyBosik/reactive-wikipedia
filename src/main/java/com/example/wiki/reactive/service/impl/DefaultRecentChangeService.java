package com.example.wiki.reactive.service.impl;

import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.repository.RecentChangeRepository;
import com.example.wiki.reactive.service.RecentChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class DefaultRecentChangeService implements RecentChangeService {
  private final RecentChangeRepository recentChangeRepository;

  @Override
  public Flux<RecentChange> getRecentChanges() {
    return recentChangeRepository.findAll();
  }
}
