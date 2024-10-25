package com.comic.server.feature.comic.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class Person implements Serializable {

  @Schema(description = "The name of the person", example = "John Doe")
  @NotBlank
  private String name;

  @Schema(description = "The description of the person", example = "A person")
  private String description;

  @Schema(description = "The image URL of the person", example = "https://example.com/image.jpg")
  private String imageUrl;

  public Person(String name) {
    this.name = name;
  }
}
