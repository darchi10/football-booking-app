package com.example.footballbooking.utilis;

import com.example.footballbooking.factory.FormatterFactory;
import com.example.footballbooking.parser.ToolResponseParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ToolResponseFormatterAdvisor implements CallAroundAdvisor {
    private final FormatterFactory formatterFactory;
    private final ToolResponseParser toolResponseParser;

    public ToolResponseFormatterAdvisor(FormatterFactory formatterFactory, ToolResponseParser toolResponseParser) {
        this.formatterFactory = formatterFactory;
        this.toolResponseParser = toolResponseParser;
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedResponse originalResponse = chain.nextAroundCall(advisedRequest);
        ChatResponse chatResponse = originalResponse.response();
        String content = chatResponse.getResult().getOutput().getText();

        System.out.println("AI answer: " + content);

        String formattedContent = formatResponse(content);

        if (!formattedContent.equals(content)) {
            ChatResponse newChatResponse = createFormattedResponse(chatResponse, formattedContent);
            return new AdvisedResponse(newChatResponse, originalResponse.adviseContext());
        }

        return originalResponse;
    }

    private String formatResponse(String content) {
        Optional<ToolResponseParser.ToolResponsePayload> payloadOptional = toolResponseParser.parse(content);

        return payloadOptional.map(payload -> formatterFactory
                .getFormatter(payload.toolUsed())
                .format(payload.data(), payload.message(), content))
                .orElse(content);
    }

    private ChatResponse createFormattedResponse(ChatResponse originalResponse, String newResponse) {
        Generation newGeneration = new Generation(new AssistantMessage(newResponse), originalResponse.getResult().getMetadata());
        return new ChatResponse(List.of(newGeneration), originalResponse.getMetadata());
    }


    @Override
    public String getName() {
        return "formatAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
