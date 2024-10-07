// package com.foodey.server.notify.httpsms;

// import com.fasterxml.jackson.annotation.JsonAlias;
// import java.time.Instant;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;

// @Getter
// @Setter
// @NoArgsConstructor
// public class HttpSMSPayload {

//   @Getter
//   @Setter
//   @NoArgsConstructor
//   public class Data {
//     @JsonAlias("phone_id")
//     private String phoneId;

//     @JsonAlias("monitor_id")
//     private String monitorId;

//     private String owner;

//     @JsonAlias("last_heartbeat_timestamp")
//     private String lastHeartbeatTimestamp;

//     private Instant timestamp;

//     @JsonAlias("user_id")
//     private String userId;
//   }

//   private Data data;
//   private String datacontenttype;
//   private String id;
//   private String source;
//   private String specversion;

//   private Instant time;

//   private String type;
// }
