package com.example.footballbooking.config;

import com.example.footballbooking.service.AiChatToolsService;
import com.example.footballbooking.utilis.ToolResponseFormatterAdvisor;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;


@Configuration
public class AIConfig {

    @Bean
    @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ChatClient.Builder chatClientBuilder(AzureOpenAiChatModel chatModel, ChatMemory chatMemory,
                                                ToolResponseFormatterAdvisor formatterAdvisor) {
        // The MessageChatMemoryAdvisor will use the default session ID provided by the session scope.
        // We set the number of previous messages to remember to 10.
        MessageChatMemoryAdvisor memoryAdvisor = new MessageChatMemoryAdvisor(chatMemory);

        return ChatClient.builder(chatModel)
                .defaultAdvisors(memoryAdvisor, formatterAdvisor);
    }


}
