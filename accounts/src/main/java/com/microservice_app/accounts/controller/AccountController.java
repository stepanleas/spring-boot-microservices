package com.microservice_app.accounts.controller;

import com.microservice_app.accounts.constants.AccountConstant;
import com.microservice_app.accounts.dto.CustomerDto;
import com.microservice_app.accounts.dto.ErrorResponseDto;
import com.microservice_app.accounts.dto.ResponseDto;
import com.microservice_app.accounts.service.IAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "CRUD REST APIs for accounts",
    description = "REST APIs for creating, fetching, updating and deleting accounts"
)
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
public class AccountController {
    private IAccountService accountService;

    @Operation(
        summary = "Create Account REST API",
        description = "REST API to create new Customer & Account"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "HTTP Status CREATED"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody CustomerDto customerDto) {
        accountService.createAccount(customerDto);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ResponseDto(AccountConstant.STATUS_201, AccountConstant.MESSAGE_201));
    }

    @Operation(
        summary = "Fetch Account Details REST API",
        description = "REST API to fetch Customer & Account details based on a mobile number"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @GetMapping("/fetch")
    public ResponseEntity<CustomerDto> fetchAccountDetails(
        @RequestParam
        @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number should be 10 digits")
        String mobileNumber
    ) {
        CustomerDto customerDto = accountService.fetchAccount(mobileNumber);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(customerDto);
    }

    @Operation(
        summary = "Update Account Details REST API",
        description = "REST API to update Customer & Account details based on a account number"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "417",
            description = "Expectation Failed"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(@Valid @RequestBody CustomerDto customerDto) {
        if (accountService.updateAccount(customerDto)) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(AccountConstant.STATUS_200, AccountConstant.MESSAGE_200));
        }

        return ResponseEntity
            .status(HttpStatus.EXPECTATION_FAILED)
            .body(new ResponseDto(AccountConstant.STATUS_417, AccountConstant.MESSAGE_417_UPDATE));
    }

    @Operation(
        summary = "Delete Account & Customer Details REST API",
        description = "REST API to delete Customer &  Account details based on a mobile number"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "HTTP Status OK"
        ),
        @ApiResponse(
            responseCode = "417",
            description = "Expectation Failed"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "HTTP Status Internal Server Error",
            content = @Content(
                schema = @Schema(implementation = ErrorResponseDto.class)
            )
        )
    })
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccountDetails(
        @RequestParam
        @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number should be 10 digits")
        String mobileNumber
    ) {
        if (accountService.deleteAccount(mobileNumber)) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(AccountConstant.STATUS_200, AccountConstant.MESSAGE_200));
        }

        return ResponseEntity
            .status(HttpStatus.EXPECTATION_FAILED)
            .body(new ResponseDto(AccountConstant.STATUS_417, AccountConstant.MESSAGE_417_DELETE));
    }
}
