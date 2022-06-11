package com.example.wiki.reactive.meta;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public enum Period {
  YEAR {
    @Override
    public long getTimestamp() {
      return getEpochSeconds(LocalDateTime.now(ZoneOffset.UTC).minusYears(1));
    }
  },
  MONTH {
    @Override
    public long getTimestamp() {
      return getEpochSeconds(LocalDateTime.now(ZoneOffset.UTC).minusMonths(1));
    }
  },
  DAY {
    @Override
    public long getTimestamp() {
      return getEpochSeconds(LocalDateTime.now(ZoneOffset.UTC).minusDays(1));
    }
  };

  public long getTimestamp() {
    return getEpochSeconds(LocalDateTime.now(ZoneOffset.UTC));
  }

  private static long getEpochSeconds(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000;
  }
}
