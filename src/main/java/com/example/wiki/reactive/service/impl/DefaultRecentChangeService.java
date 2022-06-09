package com.example.wiki.reactive.service.impl;

import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.model.UserContribution;
import com.example.wiki.reactive.repository.RecentChangeRepository;
import com.example.wiki.reactive.service.RecentChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DefaultRecentChangeService implements RecentChangeService {
  private final Flux<RecentChange> recentChangeFlux;
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

  @Override
  public Flux<UserContribution> getUserContribution(String user, long duration) {
    return Flux.concat(
        computeSnapshotContribution(user, duration),
        getRealTimeContribution(user, duration));
  }

  private Flux<UserContribution> computeSnapshotContribution(String user, long duration) {
    return recentChangeRepository.findAllByUser(user)
        .groupBy(recentChange -> computeTime(recentChange, duration))
        .flatMap(flux -> mapToMono(flux, user));
  }

  private Long computeTime(RecentChange recentChange, long duration) {
    return (recentChange.getTimestamp() / duration) * duration;
  }

  private Mono<UserContribution> mapToMono(GroupedFlux<Long, RecentChange> flux, String user) {
    return flux
        .count()
        .map(amount -> new UserContribution(user, flux.key(), amount));
  }

  private Flux<UserContribution> getRealTimeContribution(String user, long duration) {
    return recentChangeFlux
        .window(Duration.ofSeconds(duration))
        .flatMap(flux -> reduceToContribution(flux, user, duration))
        .filter(contribution -> contribution.getAmount() > 0);
  }

  private Mono<UserContribution> reduceToContribution(Flux<RecentChange> flux, String user, long duration) {
    return flux
        .filter(change -> user.equalsIgnoreCase(change.getUser()))
        .reduce(
            new UserContribution(user, Long.MAX_VALUE, 0L),
            (contribution, change) -> userContributionReducer(contribution, change, duration));
  }

  private UserContribution userContributionReducer(UserContribution contribution, RecentChange change, long duration) {
    return new UserContribution(
        contribution.getUser(),
        Math.min(contribution.getTime(), computeTime(change, duration)),
        duration
    );
  }

  private boolean ifContainsUser(Set<String> users, RecentChange recentChange) {
    return users.stream()
        .map(String::toLowerCase)
        .anyMatch(user -> user.equalsIgnoreCase(recentChange.getUser()));
  }
}
