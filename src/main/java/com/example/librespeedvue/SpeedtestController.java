package com.example.librespeedvue;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class SpeedtestController {
  /**
   * 获取客户端IP地址
   *
   * @param request HTTP请求对象，用于获取请求头信息和远程地址
   * @return 包含客户端IP地址的Map对象，key为"ip"，value为IP地址字符串
   */
  @GetMapping("/ip")
  public Map<String, Object> ip(HttpServletRequest request) {
    // 优先从X-Forwarded-For请求头获取IP地址，如果不存在则使用远程地址
    String xf = request.getHeader("X-Forwarded-For");
    String ip = xf != null ? xf.split(",")[0].trim() : request.getRemoteAddr();
    Map<String, Object> res = new HashMap<>();
    res.put("ip", ip);
    return res;
  }


  /**
   * 生成并返回指定大小的随机字节流数据
   *
   * @param size 可选参数，指定要生成的数据大小(字节)，如果未提供或小于等于0则持续生成无限流数据
   * @return ResponseEntity包装的StreamingResponseBody，包含生成的随机字节流数据
   */
  @GetMapping(value = "/garbage", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<StreamingResponseBody> garbage(@RequestParam(name = "size", required = false) Long size) {
    // 创建64KB的字节数组作为数据块
    byte[] chunk = new byte[64 * 1024];
    new Random().nextBytes(chunk);

    // 定义流式响应体的写入逻辑
    StreamingResponseBody stream = outputStream -> {
      if (size == null || size <= 0) {
        // 当size参数为空或无效时，持续写入数据块形成无限流
        try {
          while (true) {
            outputStream.write(chunk);
          }
        } catch (Exception ignored) {
        }
      } else {
        // 当size参数有效时，按指定大小写入数据
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

    // 返回包含流式数据的响应实体
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(stream);
  }


  /**
   * 处理文件上传请求
   *
   * @param request HTTP请求对象，包含上传的二进制数据流
   * @return 包含接收数据大小和处理耗时的响应结果Map
   * @throws Exception 当读取输入流或处理过程中发生错误时抛出
   */
  @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public Map<String, Object> upload(HttpServletRequest request) throws Exception {
    // 记录开始时间用于计算处理耗时
    long start = Instant.now().toEpochMilli();

    // 从请求输入流中读取所有字节数据
    byte[] data = StreamUtils.copyToByteArray(request.getInputStream());

    // 记录结束时间并计算处理耗时
    long end = Instant.now().toEpochMilli();

    // 构造响应结果，包含接收到的数据长度和处理耗时
    Map<String, Object> res = new HashMap<>();
    res.put("received", data.length);
    res.put("durationMs", end - start);
    return res;
  }


  /**
   * 获取系统配置信息
   *
   * @return 包含下载大小、上传大小、ping次数和持续时间的配置信息Map
   */
  @GetMapping("/config")
  public Map<String, Object> config() {
    Map<String, Object> res = new HashMap<>();
    // 设置下载文件大小限制为25MB
    res.put("downloadSize", 25 * 1024 * 1024);
    // 设置上传文件大小限制为8MB
    res.put("uploadSize", 8 * 1024 * 1024);
    // 设置ping测试次数为10次
    res.put("pingCount", 10);
    // 设置测试持续时间为10000毫秒
    res.put("durationMs", 10000);
    return res;
  }

}