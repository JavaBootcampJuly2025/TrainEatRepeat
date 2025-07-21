package org.athletes.traineatrepeat.config.usda;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * COMMENT: Extracting the config file into the separate package is a bit extra;
 * put it into the regular config package
 */
@Data
@Component
@ConfigurationProperties(prefix = "usda.api")
public class UsdaProperties {
    private String baseUrl;
    private String key;
}
