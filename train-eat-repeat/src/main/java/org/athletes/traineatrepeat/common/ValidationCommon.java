package org.athletes.traineatrepeat.common;

public class ValidationCommon {
  public static final String UUID_REGEX =
      "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
  public static final String NAME_REGEX = "^[A-Z][a-zA-Z '.-]*[A-Za-z][^-]$";
}
