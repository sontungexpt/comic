package com.comic.server.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

@Getter
@Setter
@NoArgsConstructor
public class Address {

  @NotBlank private String detailsAddress; // Địa chỉ chi tiết người dùng nhập vào.

  // @NotBlank
  // private String detailsAddress; // Địa chỉ chi tiết người dùng nhập vào.

  // public String getDetailsAddress() {
  //   if (StringUtils.hasText(detailsAddress)) {
  //     return detailsAddress;
  //   }
  //   return formattedAddress;
  // }

  // public String getDetailsAddress() {
  //   if (StringUtils.hasText(detailsAddress)) {
  //     return detailsAddress;
  //   }
  //   return formattedAddress;
  // }

  // @JsonValue
  // public void setDetailsAddress(String detailsAddress) {
  //   if (StringUtils.hasText(detailsAddress)) {
  //     this.detailsAddress = detailsAddress;
  //   }
  // }

  // @NotBlank private String formattedAddress; // Địa chỉ đã được định dạng theo chuẩn của Google.

  // @NotBlank private String streetNumber; // Số nhà hoặc số đường của địa chỉ.

  // @NotBlank private String route; // Tên đường.

  // @NotBlank private String city;

  // @NotBlank private String state; // Tên tiểu bang, tỉnh thành hoặc khu vực hành chính.

  // private long postalCode; // Mã bưu chính hoặc mã zip của địa chỉ.

  // @NotBlank private String country;

  @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
  private GeoJsonPoint coords; // Tọa độ địa lý của địa chỉ.

  // public Address(
  //     String formattedAddress,
  //     String streetNumber,
  //     String route,
  //     String city,
  //     String state,
  //     long postalCode,
  //     String country,
  //     GeoJsonPoint location) {
  //   this.formattedAddress = formattedAddress;
  //   this.streetNumber = streetNumber;
  //   this.route = route;
  //   this.city = city;
  //   this.state = state;
  //   this.postalCode = postalCode;
  //   this.country = country;
  // }

  @JsonCreator
  public Address(
      @NotBlank @JsonProperty("detailsAddress") String detailsAddress,
      @Min(-180) @Max(180) @NotNull @JsonProperty("longitude") Double longitude,
      @Min(-90) @Max(90) @NotNull @JsonProperty("latitude") Double latitude) {
    this.detailsAddress = detailsAddress;
    this.coords = new GeoJsonPoint(longitude, latitude);
  }

  // @JsonCreator
  // public Address(
  //     @JsonProperty("formattedAddress") String formattedAddress,
  //     @JsonProperty("streetNumber") String streetNumber,
  //     @JsonProperty("route") String route,
  //     @JsonProperty("city") String city,
  //     @JsonProperty("state") String state,
  //     @JsonProperty("postalCode") long postalCode,
  //     @JsonProperty("country") String country,
  //     @JsonProperty("longitude") Double longitude,
  //     @JsonProperty("latitude") Double latitude) {
  //   this.formattedAddress = formattedAddress;
  //   this.streetNumber = streetNumber;
  //   this.route = route;
  //   this.city = city;
  //   this.state = state;
  //   this.postalCode = postalCode;
  //   this.country = country;
  //   this.coords = new GeoJsonPoint(longitude, latitude);
  // }

  // @Override
  // public int hashCode() {
  //   return Objects.hashCode(
  //       formattedAddress, streetNumber, route, city, state, postalCode, country, coords);
  // }

  // @Override
  // public boolean equals(Object obj) {
  //   if (this == obj) return true;
  //   else if (obj == null || getClass() != obj.getClass()) return false;
  //   Address that = (Address) obj;
  //   return formattedAddress.equals(that.formattedAddress) && Objects.equal(coords, that.coords);
  // }
}
