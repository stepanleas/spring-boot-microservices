package com.microservice_app.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(
    name = "CustomerDetails",
    description = "Schema to hold Customer, Account, Cards and Loans information"
)
public class CustomerDetailsDto {
    @Schema(
        description = "Name of the customer",
        example = "John Doe"
    )
    @NotEmpty(message = "Name can not be a null or empty value")
    @Size(min = 5, max = 30, message = "Name must be between 5 and 30 characters")
    private String name;

    @Schema(
        description = "Email of the customer",
        example = "myemail@gmail.com"
    )
    @NotEmpty(message = "Email can not be a null or empty value")
    @Email(message = "Email address should be a valid value")
    private String email;

    @Schema(
        description = "Mobile number of the customer",
        example = "1234567890"
    )
    @NotEmpty(message = "Mobile number can not be a null or empty value")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
    private String mobileNumber;

    @Schema(
        description = "Account details of the customer"
    )
    private AccountDto accountDto;

    @Schema(
        description = "Loan details of the customer"
    )
    private LoanDto loanDto;

    @Schema(
        description = "Card details of the customer"
    )
    private CardDto cardDto;
}
