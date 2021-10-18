package com.example.ReactiveApi;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryMethodsTest {
    private List<String> names = Arrays.asList("Adam", "Anna", "Jake", "Jenny");

    @Test
    public void fluxUsingIterable() {
        Flux<String> namesFlux = Flux.fromIterable(names);

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna", "Jake", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxFromArray() {
        String[] names = new String[]{"Adam", "Anna", "Jake", "Jenny"};
        Flux<String> namesFlux = Flux.fromArray(names);
        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna", "Jake", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingString() {
        Flux<String> namesFlux = Flux.fromStream(names.stream());

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Anna", "Jake", "Jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingJustOrEmpty() {
        Mono<Object> mono = Mono.justOrEmpty(null);
        StepVerifier.create(mono.log())
                .verifyComplete();
    }

    @Test
    public void  monoUsingSupplier() {
        Supplier<String> stringSupplier = () -> "Adam";
        Mono<String> stringMono = Mono.fromSupplier(stringSupplier);

        StepVerifier.create(stringMono)
                .expectNext("Adam")
                .verifyComplete();
    }

    @Test
    public void fluxUsingRange() {
        Flux<Integer> integerFlux = Flux.range(1, 5);
        StepVerifier.create(integerFlux)
                .expectNext(1,2,3,4,5)
                .verifyComplete();
    }
}
