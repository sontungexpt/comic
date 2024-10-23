package com.comic.server.feature.upload;

import com.comic.server.feature.upload.dto.UploadPayload;
import com.comic.server.feature.upload.dto.UploadSignature;

public interface UploadService<T> {

  UploadSignature getUploadSignature();

  void onUploadSuccess(T t, UploadPayload payload);

  void onUploadFailure(T t, UploadPayload payload);

  void onUploadProgress(T t, UploadPayload payload);
}
