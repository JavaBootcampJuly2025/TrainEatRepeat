package org.athletes.traineatrepeat.api;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.api.model.UsdaSearchResponse;
import org.athletes.traineatrepeat.config.UsdaConfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsdaClient {

  private final UsdaConfig usdaConfig;

  private WebClient webClient() {
    return WebClient.builder()
        .baseUrl(usdaConfig.getBaseUrl())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  public UsdaSearchResponse searchFood(String query) {
    var responseMono =
        webClient()
            .get()
            .uri(
                uriBuilder ->
                    uriBuilder
                        .path(usdaConfig.getEndpoints().getFoodSearch())
                        .queryParam("api_key", usdaConfig.getKey())
                        .queryParam("query", query)
                        .queryParam("pageSize", 1)
                        .build())
            .retrieve()
            .bodyToMono(UsdaSearchResponse.class)
            .onErrorResume(e -> Mono.empty());

    return responseMono.block();
  }
}
