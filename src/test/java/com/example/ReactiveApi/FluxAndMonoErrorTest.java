package com.example.ReactiveApi;

import com.example.ReactiveApi.exception.CustomeException;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                //onErrorResume caches the exception in
                .onErrorResume(e -> {
                    System.out.println("Exception is : " + e);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
//                .expectError(RuntimeException.class)
//                .verify();
                .expectNext("default", "default1")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_onErrorReturn() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                //onErrorReturn determines the return value in case of throwing exception in flow
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_onErrorMap() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                //throw custom Exception
                .onErrorMap(e -> new CustomeException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomeException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetry() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                //throw custom Exception
                .onErrorMap(e -> new CustomeException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C") // First Retry
                .expectNext("A", "B", "C") // Second Retry
                .expectError(CustomeException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetryBackoff() {
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                //throw custom Exception
                .onErrorMap(e -> new CustomeException(e))
                //.retryBackoff(2, Duration.ofSeconds(5))  Throws IllegalStateException with message Retry exhausted and replaces CustomException
                // changed to Exceptions.RetryExhaustedException in retryWhen method
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(5)));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C") // First Retry
                .expectNext("A", "B", "C") // Second Retry
                .expectError(Exception.class)
                .verify();
    }
}
