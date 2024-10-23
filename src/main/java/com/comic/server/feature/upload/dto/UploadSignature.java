package com.comic.server.feature.upload.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@JsonIgnoreProperties(
    value = {"file", "cloudName", "apiKey", "apiSecret", "cloud_name", "api_key", "api_secret"})
public interface UploadSignature {

  String getSignature();

  Instant getTimestamp();
}
