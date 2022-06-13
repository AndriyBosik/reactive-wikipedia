package com.example.wiki.reactive.service;

import com.example.wiki.reactive.meta.Period;
import com.example.wiki.reactive.model.Contributions;
import com.example.wiki.reactive.model.MostContributedTopics;
import com.example.wiki.reactive.model.RecentChange;
import com.example.wiki.reactive.model.TopicEditions;
import com.example.wiki.reactive.model.TypedContribution;
import com.example.wiki.reactive.model.UserActivity;
import com.example.wiki.reactive.model.UserContribution;
import com.example.wiki.reactive.repository.RecentChangeRepository;
import com.example.wiki.reactive.service.impl.DefaultRecentChangeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;

class RecentChangeServiceTest {
  @Test
  void shouldReturnRepositoryContributionsWithRealTime() {
    StepVerifier.withVirtualTime(this::getUserContributions)
        .expectSubscription()
        .expectNextMatches(userContribution -> "user".equals(userContribution.getUser()) && userContribution.getAmount() == 3L)
        .expectNoEvent(Duration.ofSeconds(60))
        .expectNextMatches(userContribution -> "user".equals(userContribution.getUser()) && userContribution.getAmount() == 1L)
        .expectNoEvent(Duration.ofSeconds(60))
        .expectNextMatches(userContribution -> "user".equals(userContribution.getUser()) && userContribution.getAmount() == 2L)
        .expectNoEvent(Duration.ofSeconds(60))
        .expectNextMatches(userContribution -> "user".equals(userContribution.getUser()) && userContribution.getAmount() == 2L)
        .thenCancel()
        .verify();
  }

  @Test
  void shouldReturnUserContributionsFromRepositoryOnly() {
    LocalDateTime now = LocalDateTime.now().withHour(6).withMinute(5).withSecond(55);
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("user", now.minusSeconds(50)),
        generateRecentChange("user", now.minusSeconds(40)),
        generateRecentChange("user", now.minusSeconds(30)),
        generateRecentChange("user", now.minusSeconds(20)),
        generateRecentChange("user", now.minusSeconds(10)),
        generateRecentChange("user", now)
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByUser("user")).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);
    StepVerifier.create(recentChangeService.getUserContribution("user", 60))
        .expectSubscription()
        .expectNextMatches(userContribution -> "user".equals(userContribution.getUser()) && userContribution.getAmount() == 6L)
        .thenCancel()
        .verify();
  }

  @Test
  void shouldReturnTypedContributionsForUser() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("user", "edit"),
        generateRecentChange("user", "add"),
        generateRecentChange("user", "replace"),
        generateRecentChange("user", "add"),
        generateRecentChange("user", "add"),
        generateRecentChange("user", "comment"),
        generateRecentChange("user", "comment"),
        generateRecentChange("user", "add"),
        generateRecentChange("user", "edit"),
        generateRecentChange("user", "edit")
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByUser("user")).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getTypedContributionsForUser("user"))
        .expectSubscription()
        .expectNext(new Contributions(
            "user",
            Set.of(
                new TypedContribution("edit", 3L),
                new TypedContribution("add", 4L),
                new TypedContribution("replace", 1L),
                new TypedContribution("comment", 2L))))
        .verifyComplete();
  }

  @Test
  void shouldReturnAllMostContributedTopicForUser() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("wiki1"),
        generateRecentChange("wiki2"),
        generateRecentChange("wiki3"),
        generateRecentChange("wiki2"),
        generateRecentChange("wiki1"),
        generateRecentChange("wiki3"),
        generateRecentChange("wiki1"),
        generateRecentChange("wiki3"),
        generateRecentChange("wiki1"),
        generateRecentChange("wiki3")
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByUser("user1")).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getMostContributedTopicsForUser("user1"))
        .expectSubscription()
        .expectNext(new MostContributedTopics(
            "user1",
            4,
            Set.of("wiki1", "wiki3")))
        .verifyComplete();
  }

  @Test
  void shouldReturnOneMostContributedTopicForUser() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("wiki1"),
        generateRecentChange("wiki2"),
        generateRecentChange("wiki3"),
        generateRecentChange("wiki2"),
        generateRecentChange("wiki1"),
        generateRecentChange("wiki3"),
        generateRecentChange("wiki1"),
        generateRecentChange("wiki1"),
        generateRecentChange("wiki1"),
        generateRecentChange("wiki3")
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByUser("user1")).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getMostContributedTopicsForUser("user1"))
        .expectSubscription()
        .expectNext(new MostContributedTopics(
            "user1",
            5,
            Set.of("wiki1")))
        .verifyComplete();
  }

  @Test
  void shouldReturnMostActiveUserSortedAlphabeticallyForDay() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("user1", LocalDateTime.now().minusMinutes(1)),
        generateRecentChange("user2", LocalDateTime.now().minusMinutes(11)),
        generateRecentChange("user1", LocalDateTime.now().minusMinutes(2)),
        generateRecentChange("user2", LocalDateTime.now().minusMinutes(12)),
        generateRecentChange("user1", LocalDateTime.now().minusMinutes(4)),
        generateRecentChange("user2", LocalDateTime.now().minusMinutes(10))
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByTimestampGreaterThan(anyLong())).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getMostActiveUser(Period.YEAR))
        .expectSubscription()
        .expectNext(new UserActivity("user1", 3L))
        .verifyComplete();
  }

  @Test
  void shouldReturnMostActiveUserForDay() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("user1", LocalDateTime.now().minusMinutes(1)),
        generateRecentChange("user2", LocalDateTime.now().minusMinutes(11)),
        generateRecentChange("user1", LocalDateTime.now().minusMinutes(2)),
        generateRecentChange("user2", LocalDateTime.now().minusMinutes(12)),
        generateRecentChange("user1", LocalDateTime.now().minusMinutes(4)),
        generateRecentChange("user1", LocalDateTime.now().minusMinutes(10))
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByTimestampGreaterThan(anyLong())).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getMostActiveUser(Period.YEAR))
        .expectSubscription()
        .expectNext(new UserActivity("user1", 4L))
        .verifyComplete();
  }

  @Test
  void shouldReturnMostActiveUserForMonth() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("user1", LocalDateTime.now().minusYears(1)),
        generateRecentChange("user2", LocalDateTime.now().minusDays(3)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(2)),
        generateRecentChange("user2", LocalDateTime.now().minusDays(12)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(4)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(5)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(10))
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByTimestampGreaterThan(anyLong())).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getMostActiveUser(Period.YEAR))
        .expectSubscription()
        .expectNext(new UserActivity("user1", 5L))
        .verifyComplete();
  }

  @Test
  void shouldReturnMostActiveUserForYear() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        generateRecentChange("user1", LocalDateTime.now().minusYears(1)),
        generateRecentChange("user2", LocalDateTime.now().minusMonths(11)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(2)),
        generateRecentChange("user2", LocalDateTime.now().minusDays(12)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(4)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(5)),
        generateRecentChange("user1", LocalDateTime.now().minusDays(10))
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByTimestampGreaterThan(anyLong())).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getMostActiveUser(Period.YEAR))
        .expectSubscription()
        .expectNext(new UserActivity("user1", 5L))
        .verifyComplete();
  }

  @Test
  void shouldReturn3TopicEditions() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        new RecentChange("id2", 2L, "title1", "edit", "user1", "comment1", "wiki2", 123L),
        new RecentChange("id3", 3L, "title1", "edit", "user1", "comment1", "wiki3", 123L),
        new RecentChange("id4", 4L, "title1", "edit", "user1", "comment1", "wiki4", 123L),
        new RecentChange("id5", 5L, "title1", "edit", "user1", "comment1", "wiki3", 123L),
        new RecentChange("id9", 9L, "title1", "edit", "user1", "comment1", "wiki1", 123L),
        new RecentChange("id10", 10L, "title1", "edit", "user1", "comment1", "wiki3", 123L),
        new RecentChange("id11", 11L, "title1", "edit", "user1", "comment1", "wiki4", 123L)
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByType("edit")).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getTopTopicEditions(3))
        .expectSubscription()
        .expectNext(new TopicEditions("wiki3", 3))
        .expectNext(new TopicEditions("wiki4", 2))
        .expectNext(new TopicEditions("wiki1", 1))
        .verifyComplete();
  }

  @Test
  void shouldSortAlphabeticallyWhenAmountOfEditionsIsEqual() {
    Flux<RecentChange> recentChangeFlux = Flux.just(
        new RecentChange("id2", 2L, "title1", "edit", "user1", "comment1", "wiki2", 123L),
        new RecentChange("id3", 3L, "title1", "edit", "user1", "comment1", "wiki3", 123L),
        new RecentChange("id4", 4L, "title1", "edit", "user1", "comment1", "wiki4", 123L),
        new RecentChange("id5", 5L, "title1", "edit", "user1", "comment1", "wiki3", 123L),
        new RecentChange("id7", 7L, "title1", "edit", "user1", "comment1", "wiki2", 123L),
        new RecentChange("id9", 9L, "title1", "edit", "user1", "comment1", "wiki1", 123L),
        new RecentChange("id10", 10L, "title1", "edit", "user1", "comment1", "wiki3", 123L),
        new RecentChange("id11", 11L, "title1", "edit", "user1", "comment1", "wiki4", 123L)
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByType("edit")).thenReturn(recentChangeFlux);
    RecentChangeService recentChangeService = new DefaultRecentChangeService(Flux.empty(), repository);

    StepVerifier.create(recentChangeService.getTopTopicEditions(3))
        .expectSubscription()
        .expectNext(new TopicEditions("wiki3", 3))
        .expectNext(new TopicEditions("wiki2", 2))
        .expectNext(new TopicEditions("wiki4", 2))
        .verifyComplete();
  }

  private Flux<UserContribution> getUserContributions() {
    LocalDateTime now = LocalDateTime.now().withHour(6).withMinute(5).withSecond(55);
    Flux<RecentChange> repositoryRecentChangeFlux = Flux.just(
        generateRecentChange("user", now.minusMinutes(4).minusSeconds(40)),
        generateRecentChange("user", now.minusMinutes(4).minusSeconds(20)),
        generateRecentChange("user", now.minusMinutes(4))
    );

    RecentChangeRepository repository = Mockito.mock(RecentChangeRepository.class);
    Mockito.when(repository.findAllByUser("user")).thenReturn(repositoryRecentChangeFlux);

    Flux<RecentChange> flux = Flux.interval(Duration.ofSeconds(1))
        .filter(second -> second == 30 || second == 65 || second == 70 || second == 140 || second == 160)
        .map(second -> generateRecentChange("user", now.minusSeconds(second)));

    RecentChangeService recentChangeService = new DefaultRecentChangeService(flux, repository);

    return recentChangeService.getUserContribution("user", 60);
  }

  private RecentChange generateRecentChange(String user, String type) {
    return new RecentChange(
        "id",
        1L,
        "title",
        type,
        user,
        "comment",
        "wiki",
        1234567
    );
  }

  private RecentChange generateRecentChange(String wiki) {
    return new RecentChange(
        "id",
        1L,
        "title",
        "type",
        "user1",
        "comment",
        wiki,
        toTimestamp(LocalDateTime.now())
    );
  }

  private RecentChange generateRecentChange(String user, LocalDateTime localDateTime) {
    System.out.println(toTimestamp(localDateTime));
    return new RecentChange(
        "id",
        1L,
        "title",
        "type",
        user,
        "comment",
        "wiki",
        toTimestamp(localDateTime)
    );
  }

  private long toTimestamp(LocalDateTime localDateTime) {
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000;
  }
}
