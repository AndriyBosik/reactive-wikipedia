package com.example.wiki.reactive.service;

import org.springframework.web.reactive.function.client.WebClient;

public interface ApiClient {
  WebClient.ResponseSpec getRecentChanges();
}
