package com.comic.server.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

  public static String CLOUD_NAME;

  @Value("${cloudinary.cloud-name}")
  public void setCloudName(String cloudName) {
    CLOUD_NAME = cloudName;
  }

  @Value("${cloudinary.api-key}")
  private String API_KEY;

  public static String API_SECRET;

  @Value("${cloudinary.api-secret}")
  public void setApiSecret(String apiSecret) {
    API_SECRET = apiSecret;
  }

  @Bean
  public Cloudinary getCloudinary() {
    return new Cloudinary(
        ObjectUtils.asMap(
            "cloud_name",
            CLOUD_NAME,
            "api_key",
            API_KEY,
            "api_secret",
            API_SECRET,
            "secure",
            true));
  }
}
