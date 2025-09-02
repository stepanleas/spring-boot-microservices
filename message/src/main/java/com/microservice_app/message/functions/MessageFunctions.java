package com.microservice_app.message.functions;

import com.microservice_app.message.dto.AccountMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {
    private static final Logger logger = LoggerFactory.getLogger(MessageFunctions.class);

    @Bean
    public Function<AccountMessageDto, AccountMessageDto> email() {
        return accountMessageDto -> {
            logger.info("Sending email with the details: {}", accountMessageDto.toString());

            return accountMessageDto;
        };
    }

    @Bean
    public Function<AccountMessageDto, Long> sms() {
        return accountMessageDto -> {
            logger.info("Sending sms with the details: {}", accountMessageDto.toString());

            return accountMessageDto.accountNumber();
        };
    }
}
