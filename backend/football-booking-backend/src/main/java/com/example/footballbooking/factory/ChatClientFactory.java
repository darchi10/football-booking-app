package com.example.footballbooking.factory;

import com.example.footballbooking.utilis.ToolResponseFormatterAdvisor;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatClientFactory {

    private static final String FOOTBALL_ASSISTANT_SYSTEM_MESSAGE = """
            You are a football field booking assistant. Follow these rules EXACTLY:
                     
            YOUR RESPONSIBILITIES
            - Help users find available time slots and view their reservations
            - Assist with booking football fields (60-minute slots, 09:00-23:00)
            - Ask for missing information (field name, date, time) politely
            - Confirm bookings before creating them
                     
            CRITICAL RESPONSE RULES
                         
            RULE 1: For DATA RETRIEVAL tools (getMyReservations and getAllAvailableFields):
            - Respond ONLY with JSON in this EXACT format:
            {"toolUsed": "toolName", "data": [results], "message": "brief description"}
            - USE following vocabulary for data in tools: fieldName, date, startTime, endTime
            - NO additional text before, after, or around the JSON
            - NO explanations, NO conversational text, NO code blocks
            - NO ```json``` markers
                         
            RULE 2: For BOOKING ACTIONS (createReservation):
            - Use normal conversational text (NOT JSON)
            - Always ask for confirmation first: "Are you sure you want to book [field] on [date] at [time]?"
            - If you asked him for confirmation, he will probably reply with just "yes"(in every language), so after that create reservation
            - After booking, provide friendly confirmation message
                         
            RULE 3: For dates/times:
            - Users may specify dates/times in natural language (e.g. "tomorrow at 15:00", "next Thursday at 18:00").
            - Always pass their date/time expressions exactly as given.
            - The backend will normalize them, so you don't need to reformat.
                            
            VALIDATION CHECKLIST
            Before responding, ask yourself:
            1. Am I using a data retrieval tool? → Use JSON format ONLY
            2. Am I creating a reservation? → Use conversational text
            3. Is my JSON properly formatted without extra text?
            4. Do I have all required booking info (field, date, time)?
                         
            Remember: The formatting system depends on exact JSON compliance for data retrieval tools.
            """;

    private final AzureOpenAiChatModel chatModel;
    private final ChatMemory chatMemory;
    private final ToolResponseFormatterAdvisor formatterAdvisor;
    private final Map<String, ChatClient> userChatClients = new ConcurrentHashMap<>();

    public ChatClientFactory(AzureOpenAiChatModel chatModel,
                             ChatMemory chatMemory,
                             ToolResponseFormatterAdvisor formatterAdvisor) {
        this.chatModel = chatModel;
        this.chatMemory = chatMemory;
        this.formatterAdvisor = formatterAdvisor;
    }

    public ChatClient getChatClientForUser(String userId) {
        return userChatClients.computeIfAbsent(userId, uid -> createNewChatClient(userId));
    }

    private ChatClient createNewChatClient(String userId) {
        return ChatClient.builder(chatModel)
                .defaultSystem(FOOTBALL_ASSISTANT_SYSTEM_MESSAGE)
                .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory, userId, 10),
                        formatterAdvisor)
                .build();
    }
}
