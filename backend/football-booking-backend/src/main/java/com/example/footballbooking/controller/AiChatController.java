package com.example.footballbooking.controller;

import com.example.footballbooking.factory.ChatClientFactory;
import com.example.footballbooking.service.AiChatToolsService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private final ChatClientFactory chatClientFactory;
    private final AiChatToolsService aiToolsService;

    @Autowired
    public AiChatController(ChatClientFactory chatClientFactory, AiChatToolsService aiToolsService) {
        this.chatClientFactory = chatClientFactory;
        this.aiToolsService = aiToolsService;
    }


    @GetMapping(value = "/generate")
    public ResponseEntity<String> generate(@RequestParam("message") String message,
                                           @RequestParam("userId") String userId) {

        ChatClient chatClient = chatClientFactory.getChatClientForUser(userId);

        String response = chatClient
                .prompt()
                .user(message)
                .tools(aiToolsService)
                .call()
                .content();

        return ResponseEntity.ok(response);
    }
}