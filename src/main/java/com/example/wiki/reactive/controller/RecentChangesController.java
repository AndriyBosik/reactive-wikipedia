package com.example.wiki.reactive.controller;

import com.example.wiki.reactive.meta.Endpoint;
import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.service.RecentChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
}
