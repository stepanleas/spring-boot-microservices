package com.microservice_app.accounts.service.client;

import com.microservice_app.accounts.dto.CardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "cards", fallback = CardFallback.class)
public interface CardFeignClient {
    @GetMapping("/api/fetch")
    ResponseEntity<CardDto> fetchCardDetails(
        @RequestHeader("microservice-correlation-id") String correlationId,
        @RequestParam String mobileNumber
    );
}
