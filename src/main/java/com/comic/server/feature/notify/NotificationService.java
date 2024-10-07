// package com.foodey.server.notify;

// import java.util.concurrent.CompletableFuture;

// public interface NotificationService {

//   void sendNotification(NotificationRequest request);

//   default void sendNotificationAsync(NotificationRequest request) {
//     CompletableFuture.runAsync(() -> sendNotification(request));
//   }
// }
