package org.athletes.traineatrepeat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "usda.api")
public class UsdaConfig {
  private String baseUrl;
  private String key;
  private Endpoints endpoints;

  @Data
  public static class Endpoints {
    private String foodSearch;
  }
}
