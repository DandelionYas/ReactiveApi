package com.example.ReactiveApi;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FluxAndMonoTest {

    @Test
    public void fluxTest_Subscriber() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        flux.subscribe(System.out::println, (e)->System.err.println("Exception is " + e));
    }

    @Test
    public void fluxTestElements_WithoutError() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .log();

        StepVerifier.create(flux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .expectComplete(); //this is a call which starts the flow
    }

    @Test
    public void fluxTestElements_WithError() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(flux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
//                .expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    @Test
    public void fluxTestElements_WithError1() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();

        StepVerifier.create(flux)
                .expectNext("Spring", "Spring Boot", "Reactive Spring")
                .expectErrorMessage("Exception Occurred")
                .verify();

    }

    @Test
    public void createAFlux_just() {
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Reactive Spring");

        flux.subscribe(f -> System.out.println("I'm learning "+ f));
    }

    private class Player {
        private String firstName;
        private String lastName;

        public Player(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Player player = (Player) o;
            return Objects.equals(firstName, player.firstName) &&
                    Objects.equals(lastName, player.lastName);
        }
    }

    @Test
    public void flatMap() {
        Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
                .flatMap(n -> Mono.just(n)
                        .map(p -> {
                            String[] split = p.split("\\s");
                            return new Player(split[0], split[1]);
                        })
                        .subscribeOn(Schedulers.parallel())
                );
        List<Player> playerList = Arrays.asList(
                new Player("Michael", "Jordan"),
                new Player("Scottie", "Pippen"),
                new Player("Steve", "Kerr"));
        StepVerifier.create(playerFlux)
                .expectNextMatches(p -> playerList.contains(p))
                .expectNextMatches(p -> playerList.contains(p))
                .expectNextMatches(p -> playerList.contains(p))
                .verifyComplete();
    }

    @Test
    public void bufferAndFlatMap() {
        Flux.just("apple", "orange", "banana", "kiwi", "strawberry")
                .buffer(3)
                .flatMap(x ->
                        Flux.fromIterable(x)
                                .map(y -> y.toUpperCase())
                                .subscribeOn(Schedulers.parallel())
                                .log()
                ).subscribe();
    }
}