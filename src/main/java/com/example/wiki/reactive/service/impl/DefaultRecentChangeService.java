package com.example.wiki.reactive.service.impl;

import com.example.wiki.reactive.model.*;
import com.example.wiki.reactive.repository.RecentChangeRepository;
import com.example.wiki.reactive.service.RecentChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        computeSnapshotUserContribution(user, duration),
        getRealTimeUserContribution(user, duration));
  }

  @Override
  public Mono<Contributions> getTypedContributionsForUser(String user) {
    return recentChangeRepository.findAllByUser(user)
        .collect(Collectors.toMap(
            RecentChange::getType,
            change -> new TypedContribution(change.getType(), 1L),
            this::mergeTypedContributions))
        .map(Map::values)
        .flatMapMany(Flux::fromIterable)
        .collect(Collectors.toList())
        .map(contributions -> new Contributions(user, contributions));
  }

  @Override
  public Mono<MostContributedTopics> getMostContributedTopicsForUser(String user) {
    return recentChangeRepository.findAllByUser(user)
        .map(RecentChange::getWiki)
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
        .map(Map::entrySet)
        .flatMapMany(Flux::fromIterable)
        .groupBy(Map.Entry::getValue)
        .reduce(this::reduceToMaxContribution)
        .map(flux -> mapToMostContributedTopics(user, flux))
        .flatMap(Function.identity());
  }

  private Mono<MostContributedTopics> mapToMostContributedTopics(String user, GroupedFlux<Long, Map.Entry<String, Long>> flux) {
    return flux
        .reduce(new MostContributedTopics(user, flux.key().intValue(), new ArrayList<>()), this::reduceToMostContributedTopics);
  }

  private MostContributedTopics reduceToMostContributedTopics(MostContributedTopics contributedTopics, Map.Entry<String, Long> entry) {
    contributedTopics.getTopics().add(entry.getKey());
    return new MostContributedTopics(
        contributedTopics.getUser(),
        contributedTopics.getContributionCount(),
        contributedTopics.getTopics()
    );
  }

  private GroupedFlux<Long, Map.Entry<String, Long>> reduceToMaxContribution(
      GroupedFlux<Long, Map.Entry<String, Long>> first,
      GroupedFlux<Long, Map.Entry<String, Long>> second
  ) {
    if (first.key() > second.key()) {
      return first;
    }
    return second;
  }

  private TypedContribution mergeTypedContributions(TypedContribution first, TypedContribution second) {
    return new TypedContribution(
        first.getType(),
        first.getAmount() + second.getAmount());
  }

  private int mergeContributions(int first, RecentChange recentChange) {
    return first + 1;
  }

  private Flux<UserContribution> computeSnapshotUserContribution(String user, long duration) {
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

  private Flux<UserContribution> getRealTimeUserContribution(String user, long duration) {
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
