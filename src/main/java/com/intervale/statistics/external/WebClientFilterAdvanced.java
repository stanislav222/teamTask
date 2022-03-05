package com.intervale.statistics.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Math.min;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebClientFilterAdvanced implements ExchangeFilterFunction {

    private static final int MAX_BYTES_LOGGED = 4_096;

    @Override
    @NonNull
    public Mono<ClientResponse> filter(@NonNull ClientRequest request, @NonNull ExchangeFunction next) {
        if (!log.isInfoEnabled()) {
            return next.exchange(request);
        }

        var requestLogged = new AtomicBoolean(false);
        var responseLogged = new AtomicBoolean(false);

        var capturedRequestBody = new StringBuilder();
        var capturedResponseBody = new StringBuilder();

        var stopWatch = new StopWatch();
        stopWatch.start();

        return next
                .exchange(ClientRequest.from(request).body(new BodyInserter<>() {

                    @Override
                    @NonNull
                    public Mono<Void> insert(@NonNull ClientHttpRequest req, @NonNull Context context) {
                        return request.body().insert(new ClientHttpRequestDecorator(req) {

                            @Override
                            @NonNull
                            public Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                                return super.writeWith(Flux.from(body).doOnNext(data -> capturedRequestBody.append(extractBytes(data)))); // number of bytes appended is maxed in real code
                            }

                        }, context);
                    }
                }).build())
                .doOnNext(response -> {
                            if (!requestLogged.getAndSet(true)) {
                                log.info("=>".repeat(12) + " Request to external resource start");
                                log.info("Request: {} {}", request.method(), request.url());
                                log.info("RequestHeaders: {}", request.headers().toString());
                                log.info("Body: [{}]",  !StringUtils.isEmpty(capturedRequestBody.toString())
                                        ? capturedRequestBody.toString()
                                        : "empty");
                                log.info("..".repeat(12)  + " Request to external resource end");
                            }
                        }
                )
                .map(response -> response.mutate().body(transformer -> transformer
                                .doOnNext(body -> capturedResponseBody.append(extractBytes(body))) // number of bytes appended is maxed in real code
                                .doOnTerminate(() -> {
                                    if (stopWatch.isRunning()) {
                                        stopWatch.stop();
                                    }
                                })
                                .doOnComplete(() -> {
                                    if (!responseLogged.getAndSet(true)) {
                                        log.info("=>".repeat(12)  + " Response from external resource start");
                                        log.info("TotalTimeMillis {}", stopWatch.getTotalTimeMillis());
                                        log.info("Response status: {}", response.statusCode().value());
                                        response.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value ->
                                                log.info("Headers: {}- {}", name, value))); // filtered in real code
                                        log.info("Body: {}", capturedResponseBody.toString()); // filtered in real code
                                        log.info("..".repeat(12)  + " Response from external resource end");
                                    }
                                })
                        ).build()
                );
    }

    private static String extractBytes(DataBuffer data) {
        int currentReadPosition = data.readPosition();
        var numberOfBytesLogged = min(data.readableByteCount(), MAX_BYTES_LOGGED);
        var bytes = new byte[numberOfBytesLogged];
        data.read(bytes, 0, numberOfBytesLogged);
        data.readPosition(currentReadPosition);
        return new String(bytes);
    }

}