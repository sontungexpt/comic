// package com.foodey.server.notify.mail;

// import com.foodey.server.notify.NotificationRequest;
// import com.foodey.server.notify.NotificationService;
// import com.foodey.server.notify.NotificationType;
// import jakarta.mail.Message;
// import jakarta.mail.internet.MimeMessage;
// import lombok.RequiredArgsConstructor;
// import lombok.SneakyThrows;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.stereotype.Service;

// @RequiredArgsConstructor
// @Service(NotificationType.EMAIL)
// public class EmailNotificationServiceImpl implements NotificationService {

//   @Value("${foodey.email.from}")
//   private String sender;

//   private final JavaMailSender mailSender;

//   @Override
//   public void sendNotification(NotificationRequest request) {
//     assert request instanceof EmailRequest;
//     EmailRequest emailRequest = (EmailRequest) request;
//     switch (emailRequest.getEmailType()) {
//       case SIMPLE:
//         sendSimpleMessage(
//             emailRequest.getRecipient(), emailRequest.getSubject(), emailRequest.getMessage());
//         break;
//       case HTML:
//         sendHtmlMessage(
//             emailRequest.getRecipient(), emailRequest.getSubject(), emailRequest.getMessage());
//         break;
//       default:
//         sendSimpleMessage(
//             emailRequest.getRecipient(), emailRequest.getSubject(), emailRequest.getMessage());
//     }
//   }

//   private void sendSimpleMessage(String to, String subject, String text) {
//     SimpleMailMessage message = new SimpleMailMessage();
//     message.setTo(to);
//     message.setSubject(subject);
//     message.setText(text);
//     mailSender.send(message);
//   }

//   @SneakyThrows
//   private void sendHtmlMessage(String to, String subject, String text) {
//     MimeMessage message = mailSender.createMimeMessage();
//     message.setFrom(sender);
//     message.setRecipients(Message.RecipientType.TO, to);
//     message.setSubject(subject);
//     message.setContent(text, "text/html");
//     mailSender.send(message);
//   }
// }
