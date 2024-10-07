// package com.foodey.server.notify.httpsms;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.foodey.server.annotation.PublicEndpoint;
// import com.foodey.server.auth.enums.TokenType;
// import com.foodey.server.auth.jwt.JwtTokenException;
// import com.foodey.server.utils.HttpHeaderUtils;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.support.MissingServletRequestPartException;

// @Slf4j
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/api/v1")
// public class HttpSMSWebhookController {

//   private final HttpSMSEventService httpSMSEventService;
//   private final ObjectMapper objectMapper;

//   @PostMapping("/httpsms/webhook")
//   @PublicEndpoint
//   public void listenEvent(HttpServletRequest request) {
//     String eventType = request.getHeader("X-Event-Type");

//     try {
//       String jwtToken = HttpHeaderUtils.extractBearerToken(request);

//       // byte[] data = event.getData().toBytes();
//       // String obj = new String(data);
//       // ConsoleUtils.prettyPrint(obj);

//       // Object obj = objectMapper.readValue(data, Object.class);
//       // objectMapper.readValue(data, String.class);

//       // httpSMSEventService.validateJWT(jwtToken);
//       // httpSMSEventService.validateJWT(request);
//       httpSMSEventService.listenEvent(eventType, null);

//     } catch (MissingServletRequestPartException e) {
//       log.warn(
//           "Missing JWT token in request header for verify request from httpSMS server with ip:
// {}",
//           request.getRemoteAddr());
//       throw new JwtTokenException(TokenType.BEARER, null, "Missing JWT token in request header");
//     }
//   }
// }
