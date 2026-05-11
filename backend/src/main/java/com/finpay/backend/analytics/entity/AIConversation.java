package com.finpay.backend.analytics.entity;

import com.finpay.backend.auth.entity.User;
import com.finpay.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ai_conversations")
@Getter
@Setter
public class AIConversation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String userPrompt;

    @Column(columnDefinition = "TEXT")
    private String aiResponse;
}