package com.example.wiki.reactive.config;

import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.repository.RecentChangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
  private final Flux<RecentChange> recentChangeFlux;
  private final RecentChangeRepository recentChangeRepository;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    recentChangeFlux
        .doOnNext(this::generateId)
        .subscribe(this::saveRecentChange);
  }

  private void generateId(RecentChange recentChange) {
    recentChange.setId(UUID.randomUUID().toString());
  }

  private void saveRecentChange(RecentChange recentChange) {
    recentChangeRepository.save(recentChange)
        .subscribe();
  }
}
