package com.example.ReactiveApi;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testingWithoutVirtualTime() {
        // 3 Second of running time
        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3).log();

        StepVerifier.create(longFlux)
                .expectSubscription()
//                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }

    @Test
    public void testingWithVirtualTime() {
        // Less than one second running time
        // Use virtual time to decrease the running time of test and compile
        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1)).take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l, 1l, 2l)
                .verifyComplete();
    }
}
