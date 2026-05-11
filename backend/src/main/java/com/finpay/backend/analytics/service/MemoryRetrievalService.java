package com.finpay.backend.analytics.service;

import com.finpay.backend.analytics.dto.RetrievedMemoryResponse;
import com.finpay.backend.analytics.entity.AIConversation;
import com.finpay.backend.analytics.repository.AIConversationRepository;
import com.finpay.backend.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryRetrievalService {

    private final AIConversationRepository
            aiConversationRepository;

    public List<RetrievedMemoryResponse>
    retrieveRelevantMemory(
            User user,
            String keyword
    ) {

        List<AIConversation> conversations =
                aiConversationRepository
                        .findTop10ByUserIdAndUserPromptContainingIgnoreCase(
                                user.getId(),
                                keyword
                        );

        return conversations.stream()
                .map(conversation ->
                        new RetrievedMemoryResponse(
                                conversation.getUserPrompt(),
                                conversation.getAiResponse()
                        )
                )
                .toList();
    }
}