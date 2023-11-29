package com.example.cokkiri.controller;

import com.example.cokkiri.service.SseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class SseController {


    private Map<String, SseEmitter> sseEmitters = new ConcurrentHashMap<>();

    private  final SseService sseService;

    public SseController(SseService sseService) {
        this.sseService = sseService;
    }

    // sse 연결
    @GetMapping(value = "/subscribe/{email}",  produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable String email, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId ){
        return sseService.subscribe(email, lastEventId);
    }

}