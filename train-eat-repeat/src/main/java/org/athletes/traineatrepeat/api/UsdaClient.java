package org.athletes.traineatrepeat.api;

import lombok.RequiredArgsConstructor;
import org.athletes.traineatrepeat.config.usda.UsdaProperties;
import org.athletes.traineatrepeat.model.response.UsdaSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UsdaClient {

    private final UsdaProperties properties;

    /**
     * COMMENT: NIT. You can use {@link org.springframework.http.HttpHeaders} constant for "Content-Type" header and
     * {@link org.springframework.http.MediaType.APPLICATION_JSON_VALUE} for "application/json" header value
     */
    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    /**
     * COMMENT: NIT. You can also introduce paths for API calls, e.g. {@code /foods/search} into the application.properties.
     * You can do it under {@code usda.api.endpoints.food-search}
     */
    public UsdaSearchResponse searchFood(String query) {
        return webClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/foods/search")
                        .queryParam("api_key", properties.getKey())
                        .queryParam("query", query)
                        .queryParam("pageSize", 1)
                        .build())
                .retrieve()
                .bodyToMono(UsdaSearchResponse.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }
}
