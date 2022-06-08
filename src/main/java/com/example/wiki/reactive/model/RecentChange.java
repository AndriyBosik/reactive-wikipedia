package com.example.wiki.reactive.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "recent_changes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentChange {
  @Id
  @JsonIgnore
  private String id;
  @JsonProperty("id")
  private Long wikiId;
  private String title;
  private String type;
  private String user;
  private String comment;
  private String wiki;
  private int timestamp;
}
