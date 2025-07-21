package com.microservice_app.accounts.service.client;

import com.microservice_app.accounts.dto.LoanDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("loans")
public interface LoanFeignClient {
    @GetMapping("/api/fetch")
    ResponseEntity<LoanDto> fetchLoanDetails(@RequestParam String mobileNumber);
}
