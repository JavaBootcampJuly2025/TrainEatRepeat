package org.athletes.traineatrepeat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum Nutrients {
  PROTEIN("Protein"),
  FAT("Total lipid (fat)"),
  CARBS("Carbohydrate, by difference"),
  CALORIES("Energy");

  private final String usdaName;

  public static Nutrients fromUsdaName(String usdaName) {
    for (Nutrients nutrient : values()) {
      if (nutrient.getUsdaName().equalsIgnoreCase(usdaName)) {
        return nutrient;
      }
    }
    return null;
  }
}
