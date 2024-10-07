package com.comic.server.common.payload;

public interface IPasswordResetRequest extends IBodyRequest {

  String getPassword();

  String getConfirmPassword();
}
