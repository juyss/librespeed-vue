package com.example.librespeedvue;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class SpeedtestController {
  @GetMapping("/ip")
  public Map<String, Object> ip(HttpServletRequest request) {
    String xf = request.getHeader("X-Forwarded-For");
    String ip = xf != null ? xf.split(",")[0].trim() : request.getRemoteAddr();
    Map<String, Object> res = new HashMap<>();
    res.put("ip", ip);
    return res;
  }

  @GetMapping(value = "/garbage", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<StreamingResponseBody> garbage(@RequestParam(name = "size", required = false) Long size) {
    byte[] chunk = new byte[64 * 1024];
    new Random().nextBytes(chunk);
    StreamingResponseBody stream = outputStream -> {
      if (size == null || size <= 0) {
        try {
          while (true) {
            outputStream.write(chunk);
          }
        } catch (Exception ignored) {
        }
      } else {
        long sent = 0;
        while (sent < size) {
          long remain = size - sent;
          int toWrite = (int) Math.min(chunk.length, remain);
          outputStream.write(chunk, 0, toWrite);
          sent += toWrite;
        }
        outputStream.flush();
      }
    };
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(stream);
  }

  @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Map<String, Object> upload(HttpServletRequest request) throws Exception {
    long start = Instant.now().toEpochMilli();
    byte[] data = StreamUtils.copyToByteArray(request.getInputStream());
    long end = Instant.now().toEpochMilli();
    Map<String, Object> res = new HashMap<>();
    res.put("received", data.length);
    res.put("durationMs", end - start);
    return res;
  }

  @GetMapping("/config")
  public Map<String, Object> config() {
    Map<String, Object> res = new HashMap<>();
    res.put("downloadSize", 25 * 1024 * 1024);
    res.put("uploadSize", 8 * 1024 * 1024);
    res.put("pingCount", 10);
    res.put("durationMs", 10000);
    return res;
  }
}