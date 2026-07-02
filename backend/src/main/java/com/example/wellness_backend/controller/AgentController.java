package com.example.wellness_backend.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {

    @Autowired
    private ChatClient chatClient;
    //API test
//    @GetMapping("/ai/chat")
//    public String chat(@RequestParam String message) {
//        return chatClient.prompt()
//                .user(message)
//                .call()
//                .content();
//    }
}