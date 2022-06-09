package com.example.wiki.reactive.repository;

import com.example.wiki.reactive.model.RecentChange;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RecentChangeRepository extends ReactiveMongoRepository<RecentChange, Long> {
  @Tailable
  Flux<RecentChange> findAllBy();

  Flux<RecentChange> findAllByUser(String user);
}
