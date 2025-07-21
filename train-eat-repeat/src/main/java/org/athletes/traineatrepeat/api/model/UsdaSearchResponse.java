package org.athletes.traineatrepeat.api.model;

import java.util.List;
import lombok.Builder;

public record UsdaSearchResponse(List<Food> foods) {

  @Builder
  public record Food(
      String description,
      Float calories,
      Float protein,
      Float fat,
      Float carbohydrates,
      Integer fdcId,
      String dataType,
      List<Nutrient> foodNutrients) {
    public record Nutrient(String nutrientName, Float value) {}
  }
}
