package org.athletes.traineatrepeat.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsdaSearchResponse {
    private List<Food> foods;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Food {
        private String description;
        private Float calories;
        private Float protein;
        private Float fat;
        private Float carbohydrates;
        private Integer fdcId;
        private String dataType;
        private List<Nutrient> foodNutrients;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Nutrient {
            private String nutrientName;
            private Float value;
        }
    }
}
