package com.example.ReactiveApi;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();// not VerifyComplete Because after calling cancel complete event will not be received from publisher
    }

    @Test
    public void backPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe(element -> System.out.println("Element is : " + element)
                , e -> System.err.println("Exception is : " + e)
                , () -> System.out.println("Done") // executed onComplete() event
                , subscription -> subscription.request(2)); // Because we requesting 2 elements there is no onComplete() event
    }

    @Test
    public void backPressure_cancel() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe(element -> System.out.println("Element is : " + element)
                , e -> System.err.println("Exception is : " + e)
                , () -> System.out.println("Done") // executed onComplete() event
                , subscription -> subscription.cancel()); // Because we requesting 2 elements there is no onComplete() event
    }

    @Test
    public void customizedBackPressure() {
        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);//get elements one by one
                System.out.println("Value received is : " + value);
                if (value == 4) {
                    cancel();
                }
            }
        });
    }
}
