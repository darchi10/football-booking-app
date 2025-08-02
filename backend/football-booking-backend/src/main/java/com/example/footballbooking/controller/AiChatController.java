package com.example.footballbooking.controller;

import com.example.footballbooking.service.AiChatToolsService;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private final AzureOpenAiChatModel chatModel;
    private final AiChatToolsService aiToolsService;

    @Autowired
    public AiChatController(AzureOpenAiChatModel chatModel, AiChatToolsService aiToolsService) {
        this.chatModel = chatModel;
        this.aiToolsService = aiToolsService;
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generate(@RequestParam(value="message") String message) {
        String system = """
You're a helpful assistant for a football field booking app.
Your job is to provide and help customer with information's about bookings in our app.
They might ask you to provide free slots on this or this date, on this or this field etc.
and based on provided information's you will respond to them.
Assume each slot is 60 minutes long and the field is open from 09:00 to 23:00.
Respond clearly and concisely. Use 24-hour format. Do not add unnecessary text.
Respond only to questions regarding football booking.
""";
        String response = ChatClient.create(chatModel)
                .prompt()
                .system(system)
                .user(message)
                .tools(aiToolsService)
                .call()
                .content();
        return ResponseEntity.ok(response);
    }
}