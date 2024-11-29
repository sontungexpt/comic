// package com.comic.server.feature.auth.controller;

// import com.comic.server.annotation.CurrentUser;
// import com.comic.server.annotation.PublicEndpoint;
// import com.comic.server.utils.ConsoleUtils;
// import java.util.Map;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// // @RequestMapping("/api/v1/auth/oauth2")
// @RequiredArgsConstructor
// @RestController
// @Slf4j
// public class OAuth2Controller {

//   @PublicEndpoint
//   @GetMapping("/api/v1/auth/oauth2/code")
//   public String grantCode(@RequestParam("code") String code, @RequestParam("state") String scope)
// {
//     // log.info("code: {}", code);
//     ConsoleUtils.prettyPrint(code);
//     return "Xin chao";
//   }

//   @PublicEndpoint
//   @GetMapping("/api/v1/auth/oauth2/success")
//   public ResponseEntity<?> grantCode() {
//     return ResponseEntity.ok("Xin chao");
//   }

//   @PublicEndpoint
//   @GetMapping("/user-info")
//   public Map<String, Object> user(@CurrentUser OAuth2User principal) {
//     return principal.getAttributes();
//   }
// }
