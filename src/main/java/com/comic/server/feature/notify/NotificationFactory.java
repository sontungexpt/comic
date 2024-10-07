// package com.foodey.server.notify;

// import java.util.Map;
// import lombok.RequiredArgsConstructor;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;

// @Component
// @RequiredArgsConstructor
// public class NotificationFactory {

//   /**
//    * A map that contains NotificationService instances mapped to their corresponding notification
//    * types.
//    */
//   private final Map<String, NotificationService> notificationServiceMap;

//   /**
//    * Returns the NotificationService instance corresponding to the provided notification type.
//    *
//    * @param notificationType the type of notification
//    * @return the NotificationService instance corresponding to the provided notification type
//    * @throws IllegalArgumentException if the provided notification type is not supported
//    */
//   public NotificationService getNotificationService(String notificationType) {
//     NotificationService notificationService = notificationServiceMap.get(notificationType);

//     if (notificationService == null) {
//       throw new IllegalArgumentException("Unsupported notification type");
//     }

//     return notificationService;
//   }

//   /**
//    * Executes the sendNotification() method on the NotificationService instance corresponding to
// the
//    * provided notification type.
//    *
//    * @param notificationType the type of notification to execute
//    * @throws IllegalArgumentException if the provided notification type is not supported
//    */
//   public void execute(String notificationType, NotificationRequest notificationRequest) {
//     NotificationService notificationService = getNotificationService(notificationType);
//     notificationService.sendNotification(notificationRequest);
//   }

//   @Async
//   public void executeAsync(String notificationType, NotificationRequest notificationRequest) {
//     execute(notificationType, notificationRequest);
//   }
// }
