package com.intervale.statistics.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebClientFilter {

    public ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("=>".repeat(12) + " Request to external resource start");
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            log.info("Body: {}",  StringUtils.isEmpty(clientRequest.body().toString())
                    ? clientRequest.body().toString()
                    : "empty");
            log.info("..".repeat(12)  + " Request to external resource end");
            return Mono.just(clientRequest);
        });
    }

    public ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("=>".repeat(12)  + " Response from external resource start");
            log.info("Response status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value ->
                   log.info("Headers: {}- {}", name, value)));
            //logBody(clientResponse);
            log.info("..".repeat(12)  + " Response from external resource end");
            return Mono.just(clientResponse);
        });
    }

     private static Mono<ClientResponse> logBody(ClientResponse response) {
        if (response.statusCode() != null && (!response.statusCode().is4xxClientError() || !response.statusCode().is5xxServerError())) {
            return response.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.debug("Body is {}", body);
                        return Mono.just(response);
                    });
        } else {
            return Mono.just(response);
        }
    }

    public ExchangeFilterFunction errorResponseFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().series() == HttpStatus.Series.SERVER_ERROR) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("Server error")));
            }
            if (clientResponse.statusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new RuntimeException("API not found")));
            }
            return Mono.just(clientResponse);
        });
    }
}
