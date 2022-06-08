package com.example.wiki.reactive.meta;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Endpoint {
  private static final String API_PREFIX = "/api";

  public static final String RECENT_CHANGES = API_PREFIX + "/recent-changes";
}
