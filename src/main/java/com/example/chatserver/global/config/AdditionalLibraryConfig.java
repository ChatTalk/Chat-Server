package com.example.chatserver.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class AdditionalLibraryConfig {

    @Bean
    public MessageMatcherDelegatingAuthorizationManager.Builder builder() {
        return new MessageMatcherDelegatingAuthorizationManager.Builder();
    }
}
