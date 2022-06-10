package com.example.wiki.reactive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicsContribution {
  private String user;
  private List<TopicContribution> topicContributions = new ArrayList<>();
}
