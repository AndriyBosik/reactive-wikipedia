package com.example.wiki.reactive.controller;

import com.example.wiki.reactive.meta.Endpoint;
import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.model.Contributions;
import com.example.wiki.reactive.model.UserContribution;
import com.example.wiki.reactive.service.RecentChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@RestController
@RequestMapping(value = Endpoint.RECENT_CHANGES, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
@RequiredArgsConstructor
public class RecentChangesController {
  private final RecentChangeService recentChangeService;

  @GetMapping
  public Flux<RecentChange> get() {
    return recentChangeService.getRecentChanges();
  }

  @GetMapping("/users/{users}")
  public Flux<RecentChange> getUsersRecentChanges(@PathVariable("users") Set<String> users) {
    return recentChangeService.getUsersRecentChanges(users);
  }

  @GetMapping("/users/{user}/contribution")
  public Flux<UserContribution> getUserContribution(
      @PathVariable("user") String user,
      @RequestParam(value = "duration", defaultValue = "1") long duration
  ) {
    return recentChangeService.getUserContribution(user, duration);
  }

  @GetMapping("/users/{user}/typed-contributions")
  public Mono<Contributions> getTypedContributionsForUser(@PathVariable("user") String user) {
    return recentChangeService.getTypedContributionsForUser(user);
  }
}
