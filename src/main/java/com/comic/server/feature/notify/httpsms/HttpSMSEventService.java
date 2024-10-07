// package com.foodey.server.notify.httpsms;

// import jakarta.servlet.http.HttpServletRequest;

// public interface HttpSMSEventService {

//   /**
//    * Handle the event is emitted when your Android phone receives a new SMS. It contains the SMS
//    * sender and recipient address together with the content of the SMS.
//    *
//    * @param request
//    */
//   void handleMessagePhoneReceived(HttpServletRequest request);

//   /**
//    * Handle the event is emitted when the httpSMS app on your Android phone sends out an SMS.
//    *
//    * @param request
//    */
//   void handleMessagePhoneSent(HttpServletRequest request);

//   /**
//    * Handle the event is emitted when an SMS is delivered to the recipient's phone.
//    *
//    * @param request
//    */
//   void handleMessagePhoneDelivered(HttpServletRequest request);

//   /**
//    * Handle the event is emitted when an SMS fails to be sent out by the httpSMS app on your
// Android
//    * phone
//    *
//    * @param request
//    */
//   void handleMessagePhoneFailed(HttpServletRequest request);

//   /**
//    * Handle the event is emitted when an SMS expires before being sent out by your Android phone.
// It
//    * can happen in cases where your Android phone is powered off.
//    *
//    * @param request
//    */
//   void handleMessageSendExpired(HttpServletRequest request);

//   /**
//    * Handle the event is emitted when the httpSMS server didn't get a ping (heartbeat) from the
//    * phone in the past 1 hour. The httpSMS app on your android phone sends a ping to the server
// ever
//    * 15 minutes. If the server doesn't receive a heartbeat event in a 1 hour interval, then your
//    * phone is considered to be offline.
//    *
//    * @param request
//    */
//   void handlePhoneHeartbeatOffline(HttpServletRequest request);

//   /**
//    * Handle the event is emitted when the httpSMS server receives a heartbeat (ping) from your
//    * Android phone after it was previously offline.
//    *
//    * @param request
//    */
//   void handlePhoneHeartbeatOnline(HttpServletRequest request);

//   void validateJWT(String jwt);

//   void listenEvent(String eventType, Object payload);
// }
