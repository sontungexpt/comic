// package com.foodey.server.notify.httpsms;

// import com.foodey.server.notify.NotificationRequest;
// import com.foodey.server.notify.NotificationService;
// import com.foodey.server.notify.NotificationType;
// import com.foodey.server.utils.ConsoleUtils;
// import com.foodey.server.utils.HttpRequestUtils;
// import java.util.HashMap;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// @Slf4j
// @Service(NotificationType.SMS)
// @RequiredArgsConstructor
// public class SMSNotificationServiceImpl implements NotificationService {

//   @Value("${foodey.sms.httpsms.api-key}")
//   private String apiKey;

//   @Value("${foodey.sms.httpsms.sender}")
//   private String sender;

//   @Override
//   @Async
//   public void sendNotification(NotificationRequest request) {

//     try {
//       Object response =
//           HttpRequestUtils.post(
//               "https://api.httpsms.com/v1/messages/send",
//               new HashMap<>() {
//                 {
//                   put("content", request.getMessage());
//                   put("from", sender);
//                   put("to", (String) request.getRecipient());
//                 }
//               },
//               new HashMap<>() {
//                 {
//                   put("x-api-key", apiKey);
//                   put("Content-Type", "application/json");
//                   put("accept", "application/json");
//                 }
//               });

//       ConsoleUtils.prettyPrint(response);

//     } catch (Exception e) {
//       log.error("Error SMS " + e);
//     }
//   }
// }
