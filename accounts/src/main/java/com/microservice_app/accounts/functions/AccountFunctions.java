package com.microservice_app.accounts.functions;

import com.microservice_app.accounts.service.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountFunctions {
    private static final Logger logger = LoggerFactory.getLogger(AccountFunctions.class);

    public Consumer<Long> updateCommunication(IAccountService accountService) {
        return accountNumber -> {
            logger.info("Updating communication for the account number: {}", accountNumber);
            boolean isUpdated = accountService.updateCommunicationStatus(accountNumber);
            logger.info("Is communication status updated: {}", isUpdated);
        };
    }
}
