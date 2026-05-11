package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.AIChatRequest;
import com.finpay.backend.analytics.dto.AIChatResponse;
import com.finpay.backend.analytics.dto.AgentToolResponse;
import com.finpay.backend.analytics.dto.RetrievedMemoryResponse;
import com.finpay.backend.analytics.dto.ToolExecutionContext;
import com.finpay.backend.analytics.entity.AIConversation;
import com.finpay.backend.analytics.repository.AIConversationRepository;
import com.finpay.backend.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LLMFinancialAgentService {

    private final AnalyticsService
            analyticsService;

    private final ChatClient.Builder
            chatClientBuilder;

    private final AIConversationRepository
            aiConversationRepository;

    private final MemoryRetrievalService
            memoryRetrievalService;

    private final FinancialToolExecutorService
            financialToolExecutorService;

    public AIChatResponse chat(
            User user,
            AIChatRequest request
    ) {

        ToolExecutionContext toolContext =
                buildToolContext(
                        user,
                        request.getPrompt()
                );

        AgentToolResponse toolResponse =
                determineTool(
                        request.getPrompt(),
                        user
                );

        List<RetrievedMemoryResponse>
                retrievedMemories =
                memoryRetrievalService
                        .retrieveRelevantMemory(
                                user,
                                request.getPrompt()
                        );

        StringBuilder memoryContext =
                new StringBuilder();

        for (RetrievedMemoryResponse memory
                : retrievedMemories) {

            memoryContext.append("""
                    Relevant Previous Prompt:
                    %s

                    Relevant AI Response:
                    %s

                    """
                    .formatted(
                            memory.getUserPrompt(),
                            memory.getAiResponse()
                    ));
        }

        String context = """
                You are an AI financial assistant.

                Conversation Memory:
                %s

                Financial Summary:
                %s

                Risk Analysis:
                %s

                Behavior Analysis:
                %s

                Selected Tool:
                %s

                Tool Result:
                %s

                User Prompt:
                %s
                """
                .formatted(

                        memoryContext,

                        toolContext
                                .getFinancialSummary(),

                        toolContext
                                .getRiskAnalysis(),

                        toolContext
                                .getBehaviorAnalysis(),

                        toolResponse
                                .getToolName(),

                        toolResponse
                                .getResult(),

                        request.getPrompt()
                );

        ChatClient chatClient =
                chatClientBuilder.build();

        String response =
                chatClient.prompt()
                        .user(context)
                        .call()
                        .content();

        AIConversation conversation =
                new AIConversation();

        conversation.setUser(user);

        conversation.setUserPrompt(
                request.getPrompt()
        );

        conversation.setAiResponse(
                response
        );

        aiConversationRepository.save(
                conversation
        );

        return new AIChatResponse(
                response
        );
    }

    private ToolExecutionContext
    buildToolContext(
            User user,
            String prompt
    ) {

        String lowerPrompt =
                prompt.toLowerCase();

        ToolExecutionContext context =
                new ToolExecutionContext();

        if (
                lowerPrompt.contains("summary")
                ||
                lowerPrompt.contains("spending")
                ||
                lowerPrompt.contains("transfer")
        ) {

            context.setFinancialSummary(
                    analyticsService
                            .getFinancialSummary(
                                    user
                            )
            );
        }

        if (
                lowerPrompt.contains("risk")
                ||
                lowerPrompt.contains("fraud")
                ||
                lowerPrompt.contains("suspicious")
        ) {

            context.setRiskAnalysis(
                    analyticsService
                            .analyzeFinancialRisk(
                                    user
                            )
            );
        }

        if (
                lowerPrompt.contains("behavior")
                ||
                lowerPrompt.contains("activity")
                ||
                lowerPrompt.contains("usage")
        ) {

            context.setBehaviorAnalysis(
                    analyticsService
                            .analyzeTransactionBehavior(
                                    user
                            )
            );
        }

        return context;
    }

    private AgentToolResponse
    determineTool(
            String prompt,
            User user
    ) {

        String lowerPrompt =
                prompt.toLowerCase();

        if (
                lowerPrompt.contains("risk")
                ||
                lowerPrompt.contains("fraud")
        ) {

            return financialToolExecutorService
                    .executeTool(
                            "risk_analysis",
                            user
                    );
        }

        if (
                lowerPrompt.contains("behavior")
                ||
                lowerPrompt.contains("activity")
        ) {

            return financialToolExecutorService
                    .executeTool(
                            "behavior_analysis",
                            user
                    );
        }

        if (
                lowerPrompt.contains("recommend")
                ||
                lowerPrompt.contains("advice")
        ) {

            return financialToolExecutorService
                    .executeTool(
                            "recommendations",
                            user
                    );
        }

        return financialToolExecutorService
                .executeTool(
                        "financial_summary",
                        user
                );
    }
}