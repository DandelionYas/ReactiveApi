package com.example.ReactiveApi;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    private List<String> names = Arrays.asList("Adam", "Anna", "Jake", "Jenny");

    @Test
    public void filterTest() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(n -> n.startsWith("A"))
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna")
                .verifyComplete();
    }
    @Test
    public void filterTestLength() {
        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(n -> n.length() > 4)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("Jenny")
                .verifyComplete();
    }
}
