package com.example.wiki.reactive.repository;

import com.example.wiki.reactive.model.RecentChange;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Set;

@Repository
public interface RecentChangeRepository extends ReactiveMongoRepository<RecentChange, Long> {
  @Tailable
  Flux<RecentChange> findAllBy();

  @Tailable
  Flux<RecentChange> findAllByUserInIgnoreCase(Set<String> users);

  Flux<RecentChange> findAllByUser(String user);

  Flux<RecentChange> findAllByTimestampGreaterThan(long timestamp);

  Flux<RecentChange> findAllByType(String type);
}
