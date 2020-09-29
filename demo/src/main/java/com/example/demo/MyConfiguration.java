package com.example.demo;

import com.sun.tools.internal.ws.processor.generator.CustomExceptionGenerator;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Configuration
@AllArgsConstructor
@Slf4j
public class MyConfiguration {

    private final SslContext sslContext;

    @Bean
    ReactiveClientRegistrationRepository  getRegistration(
            @Value("${spring.security.oauth2.client.provider.authProvider.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.registration.authProvider.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.authProvider.client-secret}") String clientSecret
    ) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("authProvider")
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

    @Bean
    WebClient webClient(WebClient.Builder  webclientBuilder, ReactiveClientRegistrationRepository clientRegistrations) {
        InMemoryReactiveOAuth2AuthorizedClientService authorizedClientService = new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrations);
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrations, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(new ClientCredentialsReactiveOAuth2AuthorizedClientProvider());

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId("authProvider");

        return webclientBuilder.
                clientConnector(getClientHttpConnector(HttpClient.create(), 10000, 10000))
                .baseUrl("http://games.com/cricket")
                .filters(filters -> {
                    filters.add(oauth);
                    filters.add(logRequest());
                    filters.add(logResponse());
                }).build();
    }


    ClientHttpConnector getClientHttpConnector(HttpClient httpClient, final int connectTimeOut,  final int readTimeout) {
        httpClient.tcpConfiguration(client -> client
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut)
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)))
                .secure(spec -> spec.sslContext(sslContext)));
        return new ReactorClientHttpConnector(httpClient.wiretap(false));
    }

    ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(getClientRequestMonoFunction());
    }

    private Function<ClientRequest, Mono<ClientRequest>> getClientRequestMonoFunction() {
        return clientRequest -> {
            log.debug("Sending request: {} {}", clientRequest.method(),  clientRequest.url());
            return Mono.just(clientRequest);
        };
    }

    ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(getClientResponseMonoFunction());
    }

    private Function<ClientResponse, Mono<ClientResponse>> getClientResponseMonoFunction() {
        return clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError() || clientResponse.statusCode().is4xxClientError()) {
                log.info("Received error code: {}", clientResponse.statusCode().toString());
                return Mono.error(new Exception());
                //return Mono.error(new CustomExceptionGenerator(clientResponse.statusCode().toString());
            }else {
                log.info("Received client response with code: {}", clientResponse.statusCode().toString());
                return Mono.just(clientResponse);
            }
        };
    }


}
