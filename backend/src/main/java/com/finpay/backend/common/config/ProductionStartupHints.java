package com.finpay.backend.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Non-blocking startup hints for the {@code prod} profile only.
 */
@Component
@Profile("prod")
@Order(0)
@Slf4j
public class ProductionStartupHints implements ApplicationRunner {

    @Value("${spring.ai.openai.api-key:}")
    private String openAiApiKey;

    @Override
    public void run(ApplicationArguments args) {

        if (openAiApiKey == null || openAiApiKey.isBlank()) {

            log.warn(
                    "OPENAI_API_KEY is unset or blank; Spring AI chat and LLM endpoints "
                            + "will fail at runtime until a valid key is configured."
            );
        }
    }
}
