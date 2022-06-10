package com.example.wiki.reactive.service;

import com.example.wiki.reactive.model.MostContributedTopics;
import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.model.Contributions;
import com.example.wiki.reactive.model.UserContribution;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface RecentChangeService {
  Flux<RecentChange> getRecentChanges();

  Flux<RecentChange> getUsersRecentChanges(Set<String> users);

  Flux<UserContribution> getUserContribution(String user, long duration);

  Mono<Contributions> getTypedContributionsForUser(String user);

  Mono<MostContributedTopics> getMostContributedTopicsForUser(String user);
}
