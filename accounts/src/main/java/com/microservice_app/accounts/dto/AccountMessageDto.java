package com.microservice_app.accounts.dto;

public record AccountMessageDto(Long accountNumber, String name, String email, String mobileNumber) {
}
