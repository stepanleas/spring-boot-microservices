package com.microservice_app.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(
    name = "Account",
    description = "Schema to hold Account information"
)
@Data
public class AccountDto {

    @Schema(
        description = "Account number of the account",
        example = "3458752346"
    )
    @NotEmpty(message = "Account number can not be a null or empty value")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    private Long accountNumber;

    @Schema(
        description = "Account type of the account",
        example = "Savings"
    )
    @NotEmpty(message = "Account type can not be a null or empty value")
    private String accountType;

    @Schema(
        description = "Branch address of the account",
        example = "123 New York"
    )
    @NotEmpty(message = "Branch address can not be a null or empty value")
    private String branchAddress;
}
