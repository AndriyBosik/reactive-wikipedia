package com.example.wiki.reactive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MostContributedTopics {
  private String user;
  private int contributionCount;
  private List<String> topics;
}
