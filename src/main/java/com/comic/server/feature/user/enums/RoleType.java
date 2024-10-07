package com.comic.server.feature.user.enums;

public enum RoleType {
  ADMIN,
  CUSTOMER,
  SELLER,
  SHIPPER;

  public static class Fields {
    public static final String ADMIN = "ADMIN";
    public static final String CUSTOMER = "CUSTOMER";
    public static final String SELLER = "SELLER";
    public static final String SHIPPER = "SHIPPER";
  }
}
