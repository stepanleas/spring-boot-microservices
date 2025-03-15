package com.microservice_app.accounts.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountDto {

    @NotEmpty(message = "Account number can not be a null or empty value")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    private Long accountNumber;

    @NotEmpty(message = "Account type can not be a null or empty value")
    private String accountType;

    @NotEmpty(message = "Branch address can not be a null or empty value")
    private String branchAddress;
}
