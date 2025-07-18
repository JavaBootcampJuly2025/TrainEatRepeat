package org.athletes.traineatrepeat.config.usda;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "usda.api")
public class UsdaProperties {
    private String baseUrl;
    private String key;
}
