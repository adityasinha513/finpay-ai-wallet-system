package com.finpay.backend.analytics.repository;

import com.finpay.backend.analytics.entity.AIConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AIConversationRepository
        extends JpaRepository<AIConversation, Long> {

    List<AIConversation>
    findTop5ByUserIdOrderByCreatedAtDesc(
            Long userId
    );
    List<AIConversation>
    findTop10ByUserIdAndUserPromptContainingIgnoreCase(
            Long userId,
            String keyword
    );
}