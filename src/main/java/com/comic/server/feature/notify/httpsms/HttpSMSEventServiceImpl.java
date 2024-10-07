// package com.foodey.server.notify.httpsms;

// import com.foodey.server.auth.enums.TokenType;
// import com.foodey.server.auth.jwt.JwtService;
// import com.foodey.server.auth.jwt.JwtTokenException;
// import com.foodey.server.utils.JwtUtils;
// import jakarta.servlet.http.HttpServletRequest;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;

// @Service
// @Slf4j
// @RequiredArgsConstructor
// public class HttpSMSEventServiceImpl implements HttpSMSEventService {

//   @Value("${foodey.sms.httpsms.secret-key}")
//   private String SECRET_KEY;

//   private final JwtService jwtService;

//   @Override
//   public void handleMessagePhoneReceived(HttpServletRequest request) {}

//   @Override
//   public void handleMessagePhoneSent(HttpServletRequest request) {}

//   @Override
//   public void handleMessagePhoneDelivered(HttpServletRequest request) {}

//   @Override
//   public void handleMessagePhoneFailed(HttpServletRequest request) {}

//   @Override
//   public void handleMessageSendExpired(HttpServletRequest request) {}

//   @Override
//   public void handlePhoneHeartbeatOffline(HttpServletRequest request) {}

//   @Override
//   public void handlePhoneHeartbeatOnline(HttpServletRequest request) {}

//   @Override
//   public void validateJWT(String jwt) {
//     String signingKey = JwtUtils.extractSubject(jwt, SECRET_KEY);

//     if (!signingKey.equals(SECRET_KEY)) {
//       log.warn("Invalid JWT token in request header for verify request from httpSMS server");
//       throw new JwtTokenException(TokenType.BEARER, jwt, "Token send by httpSMS server is
// invalid");
//     }
//   }

//   @Override
//   public void listenEvent(String eventType, Object payload) {
//     log.info("Received event from httpSMS server: {}", eventType);
//   }
// }
