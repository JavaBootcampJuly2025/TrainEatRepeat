package org.athletes.traineatrepeat.common;

public class ValidationCommon {

  private ValidationCommon() {
    // Private constructor to prevent instantiation
  }

  public static final String UUID_REGEX =
      "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
  public static final String NAME_REGEX = "^[A-Z][a-zA-Z '.-][A-Za-z][^-]$";
  public static final String EMAIL_REGEX = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
  public static final String PASSWORD_REGEX =
      "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+=\\-\\[\\]{};:'\",.<>/?])[\\x20-\\x7E]{9,}$";
}
