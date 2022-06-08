package com.example.wiki.reactive.meta;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Endpoint {
  private static final String API_PREFIX = "/api";
  private static final String V1_VERSION = "/v1";
  private static final String API_V1 = API_PREFIX + V1_VERSION;

  public static final String RECENT_CHANGES = API_V1 + "/recent-changes";
}
