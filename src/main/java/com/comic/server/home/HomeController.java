package com.comic.server.home;

import com.comic.server.annotation.PublicEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

  @GetMapping("")
  @PublicEndpoint
  public String home() {
    return "Hello, Comic!";
  }

  // return an favicon.ico
  @GetMapping("favicon.ico")
  @PublicEndpoint
  public byte[] favicon() {
    return new byte[0];
  }
}
