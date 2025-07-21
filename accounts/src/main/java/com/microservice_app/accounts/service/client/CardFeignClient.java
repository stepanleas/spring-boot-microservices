package com.microservice_app.accounts.service.client;

import com.microservice_app.accounts.dto.CardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("cards")
public interface CardFeignClient {
    @GetMapping("/api/fetch")
    ResponseEntity<CardDto> fetchCardDetails(@RequestParam String mobileNumber);
}
