package com.comic.server.feature.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/.well-known")
@Slf4j
public class WellKnowController {

  @GetMapping("/assetlinks.json")
  public Object assetlinks() {
    try {
      String json =
          Files.readString(Paths.get("src/main/resources/static/.well-known/assetlinks.json"));
      return new ObjectMapper().readValue(json, Object.class);
    } catch (Exception e) {
      log.error("Error when create assetlinks", e);
      throw new RuntimeException("Error when create assetlinks");
    }
  }
}
