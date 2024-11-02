// package com.inlaco.crewmgrservice.utils;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.inlaco.crewmgrservice.exceptions.HttpRequestException;

// import java.io.IOException;
// import java.io.OutputStreamWriter;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.util.Map;
// import java.util.Scanner;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.scheduling.annotation.Async;

// public class HttpRequestUtils {

//   private static final ObjectMapper objectMapper = new ObjectMapper();

//   @Async
//   public static Object post(String url, Object body, Map<String, String> properties)
//       throws IOException {

//     HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//     con.setRequestMethod("POST");
//     properties.forEach(con::setRequestProperty);

//     // send request
//     con.setDoOutput(true);
//     try (OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream())) {
//       writer.write(objectMapper.writeValueAsString(body));
//       writer.flush();
//       writer.close();
//     }

//     boolean success = con.getResponseCode() / 100 == 2;

//     String response = "";
//     try (Scanner scanner =
//         new Scanner(success ? con.getInputStream() : con.getErrorStream()).useDelimiter("\\A")) {
//       response = scanner.hasNext() ? scanner.next() : "";
//       scanner.close();
//     }

//     if (!success) {
//       throw new HttpRequestException(
//           HttpMethod.POST, HttpStatus.valueOf(con.getResponseCode()), response);
//     }

//     return objectMapper.readValue(response, Object.class);
//   }

//   @Async
//   private static Object get(String url, Object body, Map<String, String> properties)
//       throws IOException {
//     HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
//     con.setRequestMethod("GET");
//     properties.forEach(con::setRequestProperty);

//     boolean success = con.getResponseCode() / 100 == 2;

//     String response = "";

//     try (Scanner scanner =
//         new Scanner(success ? con.getInputStream() : con.getErrorStream()).useDelimiter("\\A")) {
//       response = scanner.hasNext() ? scanner.next() : "";
//       scanner.close();
//     }

//     if (!success) {
//       throw new HttpRequestException(
//           HttpMethod.GET, HttpStatus.valueOf(con.getResponseCode()), response);
//     }

//     return objectMapper.readValue(response, Object.class);
//   }
// }
