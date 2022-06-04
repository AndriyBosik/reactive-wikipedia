package com.example.wiki.reactive.controller;

import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.service.ApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
@RequiredArgsConstructor
public class ExampleController {
  private final ApiService apiService;

  @GetMapping("/entity")
  public Flux<RecentChange> get() {
    return apiService.getRecentChanges();
  }
}
