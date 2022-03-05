package com.intervale.statistics.external.alfabank.config;

import com.intervale.statistics.external.WebClientFilter;
import com.intervale.statistics.external.WebClientFilterAdvanced;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class WebClientAlfaConfig {

    @Value("${alfa-bank.setting.base-url}")
    private String alfaBaseUrl;

    private final WebClientFilter webClientFilter;
    private final WebClientFilterAdvanced webClientFilterAdvanced;

    @Bean
    public WebClient webClientAlfaBank() {
        HttpClient httpClient = HttpClient.create()
                //время ожидания соединения
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //время ожидания ответа
                .responseTimeout(Duration.ofSeconds(5))
                //таймауты чтения и записи
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(5))
                                .addHandlerLast(new WriteTimeoutHandler(5)));
        //для отображения полных заголовков и тела запроса/ответа
        //.wiretap(this.getClass().getCanonicalName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
        return WebClient.builder()
                .baseUrl(alfaBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(webClientFilter.errorResponseFilter())
                .filter(webClientFilterAdvanced)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
