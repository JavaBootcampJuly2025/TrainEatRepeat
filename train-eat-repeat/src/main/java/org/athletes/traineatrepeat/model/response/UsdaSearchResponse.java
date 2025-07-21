package org.athletes.traineatrepeat.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * COMMENT: You can use records instead of class. Also, please use only Builder lombok annotation for records.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsdaSearchResponse {
    private List<Food> foods;


    /**
     * COMMENT: You can create a Bean for ObjectMapper to globally configure object mapper
     * <a href=https://stackoverflow.com/questions/14343477/how-do-you-globally-set-jackson-to-ignore-unknown-properties-within-spring/19554600>https://stackoverflow.com/questions/14343477/how-do-you-globally-set-jackson-to-ignore-unknown-properties-within-spring/19554600</a>
     */
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
